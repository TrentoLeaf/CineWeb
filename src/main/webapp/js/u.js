(function () {
    'use strict';

    var app = angular.module('cinema', ['usersModule', 'storageModule', 'constantsModule']);

    app.controller('MyController', ['Auth', 'StorageService', function (Auth, StorageService) {

        var ctrl = this;

        this.data = "";
        this.error = "";

        this.email= StorageService.getEmail();

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
                    StorageService.login(data);
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
                    StorageService.logout();
                },
                function () {
                    setError("logout ERROR");
                    StorageService.logout();
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
