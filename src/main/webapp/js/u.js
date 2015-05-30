(function () {
    'use strict';

    var app = angular.module('cinema', ['usersModule', 'storageModule', 'constantsModule']);

    app.controller('MyController', ['Auth', 'Users', function (Auth, Users) {

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
                    setData(data);
                },
                function (error) {
                    setError(error);
                }
            );
        };

        this.logout = function () {
            Auth.logout().then(
                function () {
                    setData("logout OK");
                },
                function () {
                    setError("logout ERROR");
                }
            )
        };

        this.change = function(email, oldPass, newPass) {
            Auth.changePassword(email, oldPass, newPass).then(
                function () {
                    setData("pass OK");
                },
                function () {
                    setError("pass ERROR");
                }
            )
        };

    }]);

})();
