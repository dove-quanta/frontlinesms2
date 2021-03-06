<%@ page import="grails.util.Environment" %>
<g:if test="${Environment.current == Environment.TEST || Environment.current.name == 'core-func-test'}">
// TODO should actually confirm that we are using the echo message source,
// rather than just in test scope, but this is unlikely to be an issue
function i18n(key) {
	var translated = key;
	if(arguments.length > 1) {
		translated += "[" + Array.prototype.slice.call(arguments, 1) + "]";
	}
	return translated;
}
</g:if>
<g:else>
var i18nStrings = i18nStrings || {};
function i18n(key) {
	var translated =
		<g:each var="plugin" in="${grailsApplication.config.frontlinesms.plugins}">
			(typeof(i18nStrings["${plugin}"])!=="undefined"? i18nStrings["${plugin}"][key]: null) ||
		</g:each>
			key;
	if(typeof(translated) == 'undefined') return key; // FIXME this line looks unnecessary
	for(i=arguments.length-1; i>0; --i) {
		translated = translated.replace("{"+(i-1)+"}", arguments[i]);
	}
	return translated;
}
</g:else>

