(function () {
    'use strict';

    angular.module('adminPlays', ['filmsPlaysModule'])

        .controller('AdminPlaysEditController', ['$routeParams', '$location', 'Plays', function($routeParams, $location, Plays) {

            var ctrl = this;
            this.currentPlay = {};

            Plays.get({id:$routeParams.uid}).$promise.then(function (data) {
                ctrl.currentPlay = data;
            }, function () {
                $location.path("/admin/plays");
            });


            this.save = function () {
                Plays.update({id: ctrl.currentPlay.pid}, ctrl.currentPlay).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                    $location.path("/admin/plays")
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };


        }])
        .controller('AdminPlaysController', ['$location', 'Plays', function ($location, Plays) {


            var ctrl = this;
            this.newPlay = new Plays();
            this.tmpPlay = {};
            this.newPlay_3d = false;


            var init = function () {

                ctrl.loading = true;
                ctrl.plays = [];
            };

            this.loadPlay = function () {

                init();

                Plays.query(function (data) {
                    ctrl.plays = data;
                    ctrl.loading = false;
                }, function (){
                    //Fail Case
                });
            };

            this.addPlay = function (data) {
                if (ctrl.newPlay_3d) {
                    ctrl.newPlay._3d = true;
                } else {
                    ctrl.newPlay._3d = false;
                };

                ctrl.newPlay.$save( function (data) {
                    ctrl.plays.push(data);
                    ctrl.newPlay = new Films();
                    console.log("Play insertion success");
                    $location.path("/admin/plays");

                }, function () {
                    //Fail Case
                    console.log("Play insertion fail");
                });
            };

            this.open_delete_modal = function (index) {
                $('#modal_deleteAgree').openModal();
                this.tmpPlay = this.users[index];
            };

            this.loadPlay();

        }]);


})();