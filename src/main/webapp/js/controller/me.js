(function () {
    'use strict';

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

            // show the password changer
            this.pass = function () {
                if (ctrl.passTrigger) { // cambio password
                    if (ctrl.newPass == ctrl.verifyPass) {
                        Auth.changePassword($rootScope.user.email, ctrl.oldPass, ctrl.newPass)
                            .success(function () {
                                ctrl.result = "Password cambiata con successo.";
                                ctrl.passBtn = "Cambia Password";
                                ctrl.passTrigger = false;
                            })
                            .error(function () {
                                ctrl.result = "La richiesta non è andata a buon fine. Ricontrolla i dati.";
                                // ctrl.passBtn = "Cambia Password"; //test
                                // ctrl.passTrigger = false; //test
                            });
                    } else {
                        ctrl.result = "Le due password inserite non coincidono.";
                    }
                } else {    // visualizza input per cambio password
                    ctrl.passTrigger = true;
                    ctrl.passBtn = "Cambia";
                }
            };


            // load the user's bookings
            this.loadUserBookings = function () {

                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                Auth.my_bookings()
                    .success(function (data) {
                        ctrl.bookings = data;

                        // calculate total of every buy
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


            // when page loads -> load user info
            $timeout(this.loadUserBookings, 1000);
        }]);

})();