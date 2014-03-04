<!-- Modal de confirmación -->
<div id="modalAcceptance" class="modal fade">
	<script type="text/javascript">
	
		function modalAcceptanceShow(title, message, acceptFunction) {
			
			var modalObject = $("#modalAcceptance");
			
			modalObject.find("#modal-question").html(title);
			modalObject.find("#modal-implication").html('<small>' + message +'</small>');
			modalObject.find("#modal-accept").on('click', acceptFunction);
			modalObject.modal('show');
		};
		
		function modalAcceptanceHide() {

			var modalObject = $("#modalAcceptance");
			modalObject.modal('hide');
		};
	</script>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Confirmaci&oacute;n</h4>
            </div>
            <div class="modal-body">
                <p id="modal-question"></p>
                <p id="modal-implication" class="text-warning"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                <button id="modal-accept" type="button" class="btn btn-primary">Aceptar</button>
            </div>
        </div>
    </div>
</div>