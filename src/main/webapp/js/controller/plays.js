(function () {
    "use strict";

    /* modulo per il caricamento delle proiezioni nella pagina principale */
    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$rootScope', '$scope', '$location', function ($rootScope, $scope, $location) {

            var ctrl = this;
            this.current = {};
            this.show_trailer_for_current = "Trailer";

            this.isNow = function (date) {
                var act = new Date();
                return true;
            };

            // imposta la proiezione selezionata da un utente
            this.setCurrent = function (date, film) {
                console.log("set current");
                ctrl.current = $rootScope.playsByDate[date].films[film];
                if ($(window).width() <= 992){  // redirect alla pagina mobile-friendly
                    $location.path("/today_mobile/").search('d', date).search('f', film);
                } else { // apertura modal dettagli film
                    $('#modal').openModal();
                }

            };

            // chiude il modal dei dettagli
            this.closeWindow = function () {
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
            });

            // init tooltip per il suggerimento di acquisto (direttiva che emette l'event alla fine del file)
            $scope.$on('tooltipRepeatEnd', function(scope, element, attrs){
                var mq = window.matchMedia("(min-width: 992px)");
                if (mq.matches) {
                    $('.tooltipped').tooltip({delay: 50});
                }
            });


            // controllo se sono sulla pagina mobile-friendly 'today_mobile'
            if (($location.search().d != undefined) && ($location.search().f != undefined)) {
                console.log("carico film mobile");
                var date = $location.search().d;
                var film = $location.search().f;

                // setta il film corrente
                ctrl.current = $rootScope.playsByDate[date].films[film];
            }

            console.log("PLAYS INIT");

        }]);

})();