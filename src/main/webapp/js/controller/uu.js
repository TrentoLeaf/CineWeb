(function () {
    'use strict';

    angular.module('test', ['usersModule'])
        .controller('TestController', ['Auth', function (Auth) {

            var ctrl = this;

            this.data = "";
            this.error = "";

            var setData = function (data) {
                ctrl.data = data;
                ctrl.error = "";
            };

            var setError = function (error) {
                ctrl.data = "";
                ctrl.error = error;
            };

            this.login = function (email, password) {
                Auth.login(email, password).then(
                    function (data) {
                        setData(data.data);
                    },
                    function (error) {
                        setError('Nome utente o password errati.');
                    }
                );
            };

            this.logout = function () {
                Auth.logout().then(
                    function () {
                        setData("Logout eseguito con successo.");
                    },
                    function () {
                        setError("Logout fallito.");
                    }
                )
            };

            this.change = function (email, oldPass, newPass) {
                Auth.changePassword(email, oldPass, newPass).then(
                    function () {
                        setData("pass OK");
                    },
                    function () {
                        setError("pass ERROR");
                    }
                )
            };

            this.me = function() {
                Auth.me().then(
                    function (data) {
                        setData(data.data);
                    },
                    function () {
                        setError("me fail");
                    }
                )
            };

        }]);

})();
