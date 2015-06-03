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
                        ctrl.user = data.data;
                        ctrl.loading = false;
                    },
                    function () {
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