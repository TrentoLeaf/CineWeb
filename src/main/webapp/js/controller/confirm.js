(function () {
    'use strict';

    angular.module('confirmModule', ['usersModule'])
        .controller('ConfirmController', ['$location', 'Auth', function ($location, Auth) {

            var ctrl = this;
            this.loading = true;
            this.status = "";

            var confirm = function () {

                // get and remove c
                var code = $location.search().c;
                //$location.search('c', null);

                // do confirm request...
                if (code == undefined) {
                    ctrl.loading = false;
                    ctrl.status = "Link non più valido o già usato.";
                } else {
                    Auth.confirmRegistration(code).then(
                        function () {
                            ctrl.loading = false;
                            ctrl.status = "Account confermato. Ora puoi acquistare biglietti!";
                        },
                        function () {
                            ctrl.loading = false;
                            ctrl.status = "Link non più valido o già usato.";
                        }
                    );
                }
            };

            // when page loads
            confirm();

        }]);
})();