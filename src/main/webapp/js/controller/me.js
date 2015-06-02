(function () {
    'use strict';

    angular.module('meModule', ['usersModule'])
        .controller('MeController', ['$location', 'Auth', function ($location, Auth) {

            var ctrl = this;
            this.user = {};

            this.loadUserData = function () {
                Auth.me().then(
                    function (data) {
                        ctrl.user = data.data;
                    },
                    function () {
                        // TODO: move this logic in appConfig
                        // on errors -> redirect to login
                        $location.path('/login');
                    }
                )
            };

            // when page loads -> load user info
            this.loadUserData();
        }]);
})();