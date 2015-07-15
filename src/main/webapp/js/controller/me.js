(function () {
    'use strict';

    /* modulo per la gestione della pagina dati utente */
    angular.module('meModule', ['usersModule'])
        .controller('MeController', ['$rootScope', '$scope', '$timeout', '$location', 'Auth', function ($rootScope, $scope, $timeout, $location, Auth) {

            var ctrl = this;
            this.loading = true;
            this.bookings = [];

            this.passTrigger = false;
            this.oldPass ="";
            this.newPass = "";
            this.verifyPass = "";
            this.result = "";
            this.passBtn = "Cambia Password";

            // mostra gli input field per il cambio password ed esegue la richiesta
            this.pass = function () {
                if (ctrl.passTrigger) { // cambio password. Esegui richiesta
                    if (ctrl.newPass == ctrl.verifyPass) {
                        Auth.changePassword($rootScope.user.email, ctrl.oldPass, ctrl.newPass)
                            .success(function () {
                                ctrl.result = "Password cambiata con successo.";
                                ctrl.passBtn = "Cambia Password";
                                ctrl.passTrigger = false;
                            })
                            .error(function () {
                                ctrl.result = "La richiesta non è andata a buon fine. Ricontrolla i dati.";
                            });
                    } else {
                        ctrl.result = "Le due password inserite non coincidono.";
                    }
                } else {    // visualizza input field per cambio password
                    ctrl.passTrigger = true;
                    ctrl.passBtn = "Cambia";
                }
            };


            // carica gli acquisti dell'utente
            this.loadUserBookings = function () {

                Auth.my_bookings()
                    .success(function (data) {
                        ctrl.bookings = data;

                        // calcola il totale di ogni acquisto
                        for (var i=0; i < ctrl.bookings.length; i++) {
                            var buy = ctrl.bookings[i];
                            var total = 0;
                            for (var j=0; j < buy.tickets.length; j++) {
                                if (buy.tickets[j].price != undefined) {
                                    total += buy.tickets[j].price;
                                }
                            }
                            buy.total = total;
                        }

                        ctrl.loading = false;
                    })
                    .error(function () {

                        ctrl.loading = false;
                    });
            };

            // init collapsible
            $scope.$on('collapsibleRepeatEnd', function(scope, element, attrs){
                $('.collapsible').collapsible({
                    accordion : false // A setting that changes the collapsible behavior to expandable instead of the default accordion style
                });
                console.log("collapsible INIZIALIZZATI");
            });

            // convert 0 to A, 1 to B, ...
            this.intToChar = function (i) {
                return String.fromCharCode('A'.charCodeAt() + parseInt(i));
            };

            // aspetta un secondoe (x animazione) e poi richiedi acquisti
            $timeout(this.loadUserBookings, 1000);
        }]);

})();