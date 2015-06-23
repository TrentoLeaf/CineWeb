(function () {
    'use strict';

    angular.module('meModule', ['usersModule'])
        .controller('MeController', ['$timeout', '$location', 'Auth', function ($timeout, $location, Auth) {

            var ctrl = this;
            this.user = {};
            this.loading = true;

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
                        $location.path('/login');
                    }
                )
            };

            // when page loads -> load user info
            $timeout(this.loadUserData, 1500);
        }]);
})();