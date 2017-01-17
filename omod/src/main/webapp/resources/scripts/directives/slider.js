'use strict';

visitNotesApp.directive('slider', function($compile){
    return {
    	scope: false,
        link: function(scope, element, attrs, controller){
            var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            function monthsBefore(d, months) {
      		  var nd = new Date(d.getTime());
      		  nd.setMonth(d.getMonth() - months);
      		  return nd;
            }
            element.dateRangeSlider({
                    bounds: { //bounds of the date range
                        min: monthsBefore(new Date(), 24),
                        max: new Date()
                    },
                    defaultValues:{
                        min: monthsBefore(new Date(), 24),
                        max: new Date()
                    },
                    formatter:function(val){
                        var days = val.getDate(),
                            month = val.getMonth() + 1,
                            year = val.getFullYear();
                        return month + "-" + days + "-" + year;
                    },
                    range:{
                    min: {months: 6},   //User has to select a minimum of 6 months
                    max: false
                    },
                    scales: [{          //displays the Jan, Feb, etc ticks on the slider
                        first: function(value){ return value; },
                        end: function(value) {return value; },
                        next: function(value){
                            var next = new Date(value);
                            return new Date(next.setMonth(value.getMonth() + 1));
                        },
                        label: function(value){
                            return months[value.getMonth()];
                        },
                        format: function(tickContainer, tickStart, tickEnd){
                            tickContainer.addClass("myCustomClass");
                        }
                    }],
                    step:{          //User can select dates in steps of 1 week
                        weeks: 1
                    }
                }
            );
            
            scope.sliderMinDate = monthsBefore(new Date(), 24);
            scope.sliderMaxDate = new Date();
        	//console.log("Initial slider min/max in slider: " + scope.sliderMinDate + " " + scope.sliderMaxDate);

            element.bind("userValuesChanged", function(e, data){
                console.log("Values just changed. min: " + data.values.min + " max: " + data.values.max);
                scope.sliderMinDate = data.values.min ;
                scope.sliderMaxDate = data.values.max ;
            	//console.log("Slider min/max in slider: " + scope.sliderMinDate + " " + scope.sliderMaxDate);
            });
        }
    }
});