(function () {
    "use strict";

    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$rootScope', '$scope', '$location', 'CompletePlays', function ($rootScope, $scope, $location, CompletePlays) {

            var ctrl = this;
            this.current = {};
            this.alternate = true;
            this.show_trailer_for_current = "Trailer";

            this.isNow = function (date) {
                var act = new Date();
                console.log("This is the date: " + date + " and this is the actual: " + act);
                return true;
            };

            this.setCurrent = function (date, film) {
                this.current = $rootScope.playsByDate[date].films[film];
                $('#modal').openModal();
            };

            this.closeWindow = function () {
                console.log("Close window...");
                $('#modal').closeModal();
            };

            this.showTrailer = function () {
                if (this.show_trailer_for_current == "Trailer") {
                    this.show_trailer_for_current = "Locandina";
                } else {
                    this.show_trailer_for_current = "Trailer";
                }
            };

            // init drowdowns (direttiva che emette l'event alla fine del file)
            $scope.$on('dropdownRepeatEnd', function(scope, element, attrs){
                $('.dropdown-button').dropdown();
                console.log("DROPDOWN INIZIALIZZATI");
            });

            // init tooltip per il suggerimento di acquisto (direttiva che emette l'event alla fine del file)
            $scope.$on('tooltipRepeatEnd', function(scope, element, attrs){
                var mq = window.matchMedia("(min-width: 992px)");
                if (mq.matches) {
                    $('.tooltipped').tooltip({delay: 50});
                    console.log("TOOLTIP INIZIALIZZATI");
                }
            });

            this.log = function () {
                console.log("pllog");
            };

        }]);

})();