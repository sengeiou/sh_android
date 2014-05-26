package com.fav24.dataservices.service.security.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.hook.GenericServiceHook;
import com.fav24.dataservices.service.generic.hook.GenericServiceHookLoader;
import com.fav24.dataservices.service.generic.impl.GenericServiceJDBCInformation;
import com.fav24.dataservices.service.security.AccessPolicyConfigurationService;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.util.FileUtils;
import com.fav24.dataservices.xml.security.AccessPolicyDOM;


/**
 * Implementación del servicio de carga y consulta de las políticas de acceso. 
 */
@Scope("singleton")
@Component
public class AccessPolicyConfigurationServiceImpl implements AccessPolicyConfigurationService {

	@Autowired
	private GenericServiceJDBCInformation entitiesInformation;
	private NavigableMap<String, GenericServiceHook> hooks;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyConfigurationServiceImpl() {

		this.hooks = new TreeMap<String, GenericServiceHook>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void dropAccessPolicies() throws ServerException {

		AccessPolicy.resetAccessPolicies();
		entitiesInformation.resetAccessPoliciesInformationAgainstDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadDefaultAccessPolicy() throws ServerException {

		AccessPolicy.resetAccessPolicies();

		try {

			AccessPolicy.loadDefaultAccessPolicies();
			entitiesInformation.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
		}
		catch(ServerException e) {

			AccessPolicy.resetAccessPolicies();
			entitiesInformation.resetAccessPoliciesInformationAgainstDataSource();

			throw e;
		}
	}

	/**
	 * {@inheritDocs}
	 */
	@Override
	public synchronized RemoteFiles loadAccessPolicy(RemoteFiles accessPolicyFiles) throws ServerException {

		if (accessPolicyFiles == null || accessPolicyFiles.getURLs() == null || accessPolicyFiles.getURLs().length == 0) {

			loadDefaultAccessPolicy();
		}
		else {

			for(URL url : accessPolicyFiles.getURLs()) {

				AccessPolicy accessPolicy = new AccessPolicyDOM(url);

				entitiesInformation.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

				AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);
			}
		}

		return accessPolicyFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicyDOM(accessPolicyStream);

		entitiesInformation.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

		AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);

		return accessPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AccessPolicy getCurrentAccessPolicy(AccessPolicy accessPolicy) throws ServerException {

		if (AccessPolicy.getCurrentAccesPolicy() == null || 
				AccessPolicy.getCurrentAccesPolicy().getAccessPolicies() == null || 
				AccessPolicy.getCurrentAccesPolicy().getAccessPolicies().isEmpty()) {

			throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED, AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);
		}

		if (accessPolicy.getAccessPolicies() == null || accessPolicy.getAccessPolicies().size() == 0) {
			accessPolicy.setAccessPolicies(AccessPolicy.getCurrentAccesPolicy().getAccessPolicies());
		}
		else {
			Iterator<EntityAccessPolicy> entityAccessPolicies = accessPolicy.getAccessPolicies().iterator();
			while (entityAccessPolicies.hasNext()) {

				EntityAccessPolicy entityAccessPolicy = entityAccessPolicies.next();
				EntityAccessPolicy currentEntityAccessPolicy = AccessPolicy.getEntityPolicy(entityAccessPolicy.getName().getAlias());

				if (currentEntityAccessPolicy != null) {
					entityAccessPolicy.getName().setName(currentEntityAccessPolicy.getName().getName());
					entityAccessPolicy.setAllowedOperations(currentEntityAccessPolicy.getAllowedOperations());
					entityAccessPolicy.setOnlyByKey(currentEntityAccessPolicy.getOnlyByKey());
					entityAccessPolicy.setOnlySpecifiedFilters(currentEntityAccessPolicy.getOnlySpecifiedFilters());
					entityAccessPolicy.setMaxPageSize(currentEntityAccessPolicy.getMaxPageSize());
					entityAccessPolicy.setData(currentEntityAccessPolicy.getData());
					entityAccessPolicy.setKeys(currentEntityAccessPolicy.getKeys());
					entityAccessPolicy.setFilters(currentEntityAccessPolicy.getFilters());
				}
				else {
					entityAccessPolicies.remove();
				}
			}
		}

		return accessPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AbstractList<String> getPublicEntities() {

		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getCurrentAccesPolicy().getEnititiesAliases() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized EntityAccessPolicy getPublicEntityPolicy(String entity) {

		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getEntityPolicy(entity) : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadDefaultHooks() throws ServerException {

		String applicationHome = DataServicesContext.getCurrentDataServicesContext().getApplicationHome();

		// Se cargan los archivos de políticas de seguridad existentes.
		File applicationHomeDir = new File(applicationHome);

		if (applicationHomeDir.exists() && applicationHomeDir.isDirectory()) {

			AbstractList<File> hookSourceFiles = FileUtils.getFilesWithSuffix(applicationHome, ".java", null);

			if (hookSourceFiles.size() == 0) {

				throw new ServerException(AccessPolicyService.ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD, 
						AccessPolicyService.ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD_MESSAGE);
			}
			else {

				URL urls[] = new URL[hookSourceFiles.size()];
				int i=0;
				for(File hookSourceFile : hookSourceFiles) {

					try {

						urls[i++] = hookSourceFile.toURI().toURL();
					} 
					catch (MalformedURLException e) {
						throw new ServerException(AccessPolicyService.ERROR_INVALID_HOOK_FILE_URL, 
								String.format(AccessPolicyService.ERROR_INVALID_HOOK_FILE_URL_MESSAGE, hookSourceFile.toURI().toString()));
					}
				}

				loadHooks(urls);
			}
		}
		else {

			throw new ServerException(DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
					DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
		}
	}

	/**
	 * {@inheritDocs}
	 */
	@Override
	public synchronized RemoteFiles loadHooks(RemoteFiles hookFiles) throws ServerException {

		if (hookFiles == null || hookFiles.getURLs() == null || hookFiles.getURLs().length == 0) {

			loadDefaultHooks();
		}
		else {

			loadHooks(hookFiles.getURLs());
		}

		return hookFiles;
	}

	/**
	 * {@inheritDocs}
	 */
	private void loadHooks(URL[] sources) throws ServerException {

		if (sources != null && sources.length > 0) {

			Map<String, StringBuilder> compilerDiagnostics = GenericServiceHookLoader.compile(sources);

			if (compilerDiagnostics != null && compilerDiagnostics.size() > 0) {

				StringBuilder diagnostics = new StringBuilder();

				for (StringBuilder diagnostic : compilerDiagnostics.values()) {
					diagnostics.append(diagnostic);
				}

				throw new ServerException(ERROR_HOOK_LOAD, ERROR_HOOK_LOAD_MESSAGE);
			}

			AbstractList<GenericServiceHook> loadedHooks = GenericServiceHookLoader.load(sources);

			for(GenericServiceHook loadedHook : loadedHooks) {

				hooks.put(loadedHook.getAlias(), loadedHook);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void dropHooks() throws ServerException {

		hooks.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized NavigableMap<String, GenericServiceHook> getAvailableHooks() {

		return hooks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized GenericServiceHook getHook(String alias) {

		return hooks.get(alias);
	}
}
