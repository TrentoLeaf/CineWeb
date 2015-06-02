(function () {
    'use strict';

    angular.module('adminUsers', ['usersModule'])
        .controller('AdminUsersController', ['$location', 'Users', function ($location, Users) {

            var ctrl = this;
            this.order = 'uid';
            this.reverse = false;

            this.newUser = new Users();
            this.newUser.role = "client";

            var init = function () {
                ctrl.loading = true;
                ctrl.users = [];
            };

            // order list
            this.setOrder = function (order) {
                if (this.order === order) {
                    this.reverse = !this.reverse;
                } else {
                    this.reverse = false;
                    this.order = order;
                }
            };

            // load the users list
            this.loadUsers = function () {
                init();
                Users.query(function (data) {
                    ctrl.users = data;
                    ctrl.loading = false;
                });
            };

            // remove a user
            this.deleteUser = function (user) {
                // todo -> prompt dialog
                // better BEFORE calling this function
                Users.delete({id: user.uid}, function () {
                    // ok
                    ctrl.users.splice(ctrl.users.indexOf(user), 1);
                }, function () {
                    // fail
                });
            };

            // save the current user
            this.addUser = function () {
                this.newUser.$save(function (data) {
                    ctrl.users.push(data);
                    ctrl.newUser = new Users();
                    ctrl.newUser.role = "client";
                }, function () {
                    // errors
                });
            };

            // edit a given user
            this.editUser = function (user) {
                user.firstName = Math.random().toString(36).substring(7);
                Users.update({id: user.uid}, user).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };

            // load data at start
            this.loadUsers();

        }]);

})();