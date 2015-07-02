(function () {
    'use strict';

    angular.module('meModule', ['usersModule'])
        .controller('MeController', ['$rootScope', '$timeout', '$location', 'Auth', function ($rootScope, $timeout, $location, Auth) {

            var ctrl = this;
            this.user = {};
            this.loading = true;
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
                    Auth.changePassword(user.email, ctrl.oldPass, ctrl.newPass)
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


            // TODO tutto da rifare, bisogna richiedere tutti i dati utente
            this.loadUserData = function () {
                Auth.me().then(
                    function (data) {
                        console.log("ME LOGIN DATA retrived ");
                        console.log(data);

                        //save basic user data
                        ctrl.user = data;

                        ctrl.loading = false;
                    },
                    function () {
                        console.log("ME LOGIN DATA NOT retrived");
                        // set logged var
                        ctrl.logged = false;
                        ctrl.user = {};

                        // TODO: move this logic in appConfig
                        // on errors -> redirect to login
                        $rootScope.afterLogin = "userArea";
                        $location.path('/login');
                    }
                )
            };

            // when page loads -> load user info
            $timeout(this.loadUserData, 1500);
        }]);
})();