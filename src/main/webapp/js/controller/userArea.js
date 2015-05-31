/**
 * Created by stefano on 31/05/15.
 */
(function(){
    'use strict';

    var app=angular.module('usermodule', []).controller('UserController', function(){
        this.user=person;

    });
    var person = {
        firstName:'pippo',
        secondName: 'pluto',   <!-- Poi verranno gestite con chiamate al database -->
        email: 'paperino',
        credit: '13',
    };

})();