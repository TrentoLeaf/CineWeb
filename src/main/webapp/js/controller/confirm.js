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
                    ctrl.status = "Already confirmed!";
                } else {
                    Auth.confirmRegistration(code).then(
                        function () {
                            ctrl.loading = false;
                            ctrl.status = "Confirmed!";
                        },
                        function () {
                            ctrl.loading = false;
                            ctrl.status = "KO!";
                        }
                    );
                }
            };

            // when page loads
            confirm();

        }]);
})();