<!-- Modal de confirmación -->
<div id="modalAcceptance" class="modal fade">
	<script type="text/javascript">
	
		function modalAcceptanceShow(title, message, acceptFunction) {
			
			var modalObject = $("#modalAcceptance");
			
			modalObject.find("#modal-question").html(title);
			modalObject.find("#modal-implication").html('<small>' + message +'</small>');
			modalObject.find("#modal-accept").off('click');
			modalObject.find("#modal-accept").on('click', function(event) { event.stopImmediatePropagation(); modalAcceptanceDisable(); acceptFunction(); modalAcceptanceHide(); modalAcceptanceEnable();});
			modalObject.modal('show');
		};
		
		function modalAcceptanceDisable() {

			var modalObject = $("#modalAcceptance");
			modalObject.find("#modal-accept").prop('disabled', true);
			modalObject.find("#modal-decline").prop('disabled', true);
			modalObject.find("#modal-close").prop('disabled', true);
		};

		function modalAcceptanceEnable(event) {

			var modalObject = $("#modalAcceptance");
			modalObject.find("#modal-accept").prop('disabled', false);
			modalObject.find("#modal-decline").prop('disabled', false);
			modalObject.find("#modal-close").prop('disabled', false);
		};
		
		function modalAcceptanceHide() {

			var modalObject = $("#modalAcceptance");
			modalObject.modal('hide');
		};
	</script>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button id="modal-close" type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Confirmaci&oacute;n</h4>
            </div>
            <div class="modal-body">
                <p id="modal-question"></p>
                <p id="modal-implication" class="text-warning"></p>
            </div>
            <div class="modal-footer">
                <button id="modal-decline" type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                <button id="modal-accept" type="button" class="btn btn-primary">Aceptar</button>
            </div>
        </div>
    </div>
</div>