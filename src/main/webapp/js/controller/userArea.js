(function () {
    'use strict';

    angular.module('usermodule', ['usersModule'])
        .controller('UserController', ['$location', 'Auth', function ($location, Auth) {

            var ctrl = this;
            this.user = {};

            this.loadUserData = function () {
                Auth.me().then(
                    function (data) {
                        ctrl.user = data.data;
                    },
                    function () {
                        // on errors -> redirect to login
                        $location.path('/login');
                    }
                )
            };

            // when page loads -> load user info
            this.loadUserData();

        }]);
})();