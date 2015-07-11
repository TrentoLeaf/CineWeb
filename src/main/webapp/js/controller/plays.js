(function () {
    "use strict";

    /* modulo per il caricamento delle proiezioni nella pagina principale */
    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$rootScope', '$scope', function ($rootScope, $scope) {

            var ctrl = this;
            this.current = {};
            this.show_trailer_for_current = "Trailer";

            // TODO ???
            this.isNow = function (date) {
                var act = new Date();
                console.log("This is the date: " + date + " and this is the actual: " + act);
                return true;
            };

            // imposta la proiezione selezionata da un utente
            this.setCurrent = function (date, film) {
                this.current = $rootScope.playsByDate[date].films[film];
                $('#modal').openModal();
            };

            // chiude il modal dei dettagli
            this.closeWindow = function () {
                console.log("Close window...");
                $('#modal').closeModal();
            };

            // swap locandina - trailer
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

        }]);

})();