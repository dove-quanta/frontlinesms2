<div id="sorting">
	<h2><g:message code="poll.sort.header"/></h2>
	<div class="info">
		<p><g:message code="poll.sort.description"/></p>
	</div>

	<div>
		<ul class="input sorting-options">
			<li>
				<g:radio name="enableKeyword" value="enabled" id="yesAutosort"
				checked="${activityInstanceToEdit && (activityInstanceToEdit?.keywords?.size() > 0) }"/>
				<label class="sorting-option-label"><g:message code="activity.generic.enable.sorting"/></label>
				<div class="sorting-option">
					<label><g:message code="activity.generic.keywords.title"/></label>
				</div>
			</li>
			<li>
				<g:radio name="enableKeyword" value="disabled" id="noAutosort"
				checked="${!(activityInstanceToEdit && (activityInstanceToEdit?.keywords?.size() > 0))}"/>
				<label class="sorting-option-label"><g:message code="activity.generic.disable.sorting"/></label>
				<div class="sorting-option">
					<label><g:message code="activity.generic.disable.sorting.description"/></label>
				</div>
			</li>
		</ul>
	</div>
	<r:script>
		var enableKeyword = function() {
				var enabled = $(this).val();
				if(enabled == "enabled") {
					$('#poll-keyword').removeAttr("disabled");
					$('#sorting-details').show();
					$('input:not(:disabled).keywords').addClass('required');
				}
				else {
					$('#poll-keyword').attr("disabled", "disabled");
					$('#sorting-details').hide();
					$('input:not(:disabled).keywords').removeClass('required');
				}
			};
		$("input[name='enableKeyword']").live("change", enableKeyword);
	</r:script>

</div>
<div id="sorting-details">
	<h2><g:message code="subscription.top.keyword.header"/></h2>
	<div class="info">
		<p><g:message code="subscription.top.keyword.description"/></p>
	</div>
	<div class="input">
		<g:textField name="topLevelKeyword" id="poll-keyword" class="sorting-generic-no-spaces sorting-generic-unique validcommas" disabled="${activityInstanceToEdit?.keywords?false:true}" value="${activityInstanceToEdit?.keywords.findAll{it.isTopLevel && it.ownerDetail == null}?.value?.join(',')}"/>
	</div>
	<h2><g:message code="poll.keywords.header"/></h2>
	<div class="info">
		<p><g:message code="poll.aliases.prompt.details"/></p>
	</div>
	<div class="input">
		<table id='poll-aliases'>
			<g:each in="${['A','B','C','D','E']}" var="key" status="i">
				<tr>
					<g:if test="${activityInstanceToEdit?.id}">
						<td><label for='keywords${key}' class="${key == 'A' || key == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)) ? 'field-enabled': ''}">keywords${key}</label>
						<% def pollResponse = activityInstanceToEdit?.responses.find {it.key == key} %>
						<g:if test="${(key == 'A' || key == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 2)))}"></td>
							<td><g:textField class='keywords required validcommas sorting-generic-no-spaces' name='keywords${key}' value="${activityInstanceToEdit.keywords.findAll{ it.ownerDetail == pollResponse?.key }.value.join(',')}"/></td>
						</g:if>
						<g:else>
							<td><g:textField class='keywords required validcommas sorting-generic-no-spaces' name="keywords${key}" value="" disabled="true"/></td>
						</g:else>
					</g:if>
					<g:else>
						<td><label for='keywords${key}' class="${key == 'A' || key == 'B' ? 'field-enabled': ''}">${option}</label></td>
						<g:if test="${key == 'A' || key == 'B'}">
							<td><g:textField class='keywords validcommas sorting-generic-no-spaces' name='keywords${key}'/></td>
						</g:if>
						<g:else>
							<td><g:textField class='keywords validcommas sorting-generic-no-spaces' name='keywords${key}' disabled="true"/></td>
						</g:else>
					</g:else>
				</tr>
			</g:each>
		</table>
	</div>
</div>