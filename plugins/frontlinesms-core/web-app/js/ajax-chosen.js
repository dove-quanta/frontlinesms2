ajaxChosen = (function() {
	var init = function() {
		return ($.fn.ajaxChosen = function(settings, callback, chosenOptions) {
			var chosenXhr, defaultOptions, options, select;
			settings = settings || {};
			chosenOptions = chosenOptions || function() {};
			defaultOptions = {
				minTermLength: 3,
		       afterTypeDelay: 500,
		       jsonTermKey: "term",
		       keepTypingMsg: "Keep typing...",
		       lookingForMsg: "Looking for"
			};
			select = this;
			chosenXhr = null;
			options = $.extend({}, defaultOptions, $(select).data(), settings);
			this.chosen(chosenOptions);
			return this.each(function() {
				return $(this).next('.chzn-container').find(".search-field > input, .chzn-search > input").bind('keyup', function() {
					var field, msg, success, untrimmed_val, val;
					untrimmed_val = $(this).attr('value');
					val = $.trim($(this).attr('value'));
					msg = val.length < options.minTermLength ? options.keepTypingMsg : options.lookingForMsg + (" '" + val + "'");
					select.next('.chzn-container').find('.no-results').text(msg);
					if (val === $(this).data('prevVal')) {
						return false;
					}
					$(this).data('prevVal', val);
					if (this.timer) {
						clearTimeout(this.timer);
					}
					if (val.length < options.minTermLength) {
						return false;
					}
					field = $(this);
					options.data = options.data || {};
					options.data[options.jsonTermKey] = val;
					if (options.sendSelectedSoFarOnEachLookup) {
						options.data.selectedSoFar = select.val();
					}
					if (options.dataCallback) {
						options.data = options.dataCallback(options.data);
					}
					success = options.success;
					options.success = function(data) {
						var requestString, items, nbItems, selected_values;
						if(!data) {
							return;
						}
						selected_values = [];
						select.find('option').each(function() {
							if ($(this).is(":selected")) {
								return selected_values.push($(this).val() + "-" + $(this).text());
							}
							return $(this).remove();
						});
						select.find('optgroup:empty').each(function() {
							$(this).remove();
						});
						requestString = data.query;
						data = data.results;
						if (requestString !== field.attr('value')) {
							console.log("NOT EQUAL, should quit");
							console.log("select's value is currently " + field.attr('value'));
							console.log("query's value was " + requestString);
							return false;
						}
						items = callback? callback(data): data;
						nbItems = 0;
						$.each(items, function(i, element) {
							var group, text, value;
							nbItems++;
							if (element.group) {
								group = select.find("optgroup[label='" + element.text + "']");
								if (!group.size()) {
									group = $("<optgroup />");
								}
								group.attr('label', element.text).appendTo(select);
								return $.each(element.items, function(i, element) {
									var text, value;
									if (typeof element === "string") {
										value = i;
										text = element;
									} else {
										value = element.value;
										text = element.text;
									}
									if ($.inArray(value + "-" + text, selected_values) === -1) {
										return $("<option />").attr('value', value).html(text).appendTo(group);
									}
								});
							} else {
								if (typeof element === "string") {
									value = i;
									text = element;
								} else {
									value = element.value;
									text = element.text;
								}
								if ($.inArray(value + "-" + text, selected_values) === -1) {
									return $("<option />").attr('value', value).html(text).appendTo(select);
								}
							}
						});
						if (nbItems) {
							select.trigger("liszt:updated");
						} else {
							select.data().chosen.no_results_clear();
							select.data().chosen.no_results(field.attr('value'));
						}
						return true;
					};
					return (this.timer = setTimeout(function() {
						if (chosenXhr) {
							chosenXhr.abort();
						}
						return (chosenXhr = $.ajax(options));
					}, options.afterTypeDelay));
				});
			});
		});
	};
	return { init: init };
}());

