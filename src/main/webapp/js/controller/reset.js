(function () {
    'use strict';

    angular.module('resetModule', ['usersModule'])
        .controller('ResetController', ['$location', 'Auth', function ($location, Auth) {

            var ctrl = this;

            this.status = "";
            this.email = "";
            this.password = "";
            this.verifyPassword = "";

            this.submit = function () {

                // get and remove r
                var code = $location.search().r;
                //$location.search('c', null);

                // do confirm request...
                if (code == undefined) {
                    ctrl.loading = false;
                    ctrl.status = "Link non più valido.";
                } else {
                    if (ctrl.verifyPassword == ctrl.password) {
                        Auth.changePasswordWithCode(ctrl.email, code, ctrl.password).then(
                            function () {
                                ctrl.loading = false;
                                ctrl.status = "Richiesta riuscita. Usa la nuova password per entrare nel tuo account.";
                            },
                            function () {
                                ctrl.loading = false;
                                ctrl.status = "La richiesta non è andata a buon fine. Ricontrolla i dati o, se il problema persiste, ricomincia la procedura di recupero.";
                            }
                        );
                    } else {
                        this.status = "Le due password inserite non coincidono.";
                    }
                }
            };

        }]);
})();