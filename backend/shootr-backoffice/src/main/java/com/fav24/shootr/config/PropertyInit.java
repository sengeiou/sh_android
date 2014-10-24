package com.fav24.shootr.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PropertyInit implements InitializingBean {


    @Autowired
    private ApplicationContext appContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //changes the timezone --> same as VM option -Duser.timezone=UTC
        //System.setProperty("user.timezone","UTC");
        //System.out.println("Custom Timezone Set!");
        //appContext.getEnvironment().getProperty()

    }
}
