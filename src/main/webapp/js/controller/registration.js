(function () {
    'use strict';

    var app = angular.module('registration-module', [])

        .controller('registrationCtrl', function () {

        this.user = "";

        this.submit = function () {
            console.log(this.user);
            // TODO! call backend
        };

})


});