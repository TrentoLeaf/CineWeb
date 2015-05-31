(function () {
    'use strict';

    var app = angular.module('loginModule', ['usersModule', 'storageModule', 'constantsModule']);

    app.controller('LoginController', ['$location', 'Auth', 'StorageService', function ($location, Auth, StorageService) {

        var ctrl = this;

        this.user = {};

        this.data = "";
        this.error = "";

        this.email = StorageService.getEmail();

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
                    setError('Nome utente o password errati.');
                }
            );
        };

        this.logout = function () {
            Auth.logout().then(
                function () {
                    setData("Logout eseguito con successo.");
                    StorageService.logout();
                },
                function () {
                    setError("Logout fallito.");
                    StorageService.logout();
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

        this.losePass = function (email) {
            // redirect to a new partial
        };

        this.register = function (email) {
            // redirect al partial registrazione
            $location.path('/registration');
        };


    }]);

})();
