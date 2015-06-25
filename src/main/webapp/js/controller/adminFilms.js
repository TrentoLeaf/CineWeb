(function () {
    'use strict';

    angular.module('adminFilms', ['filmsPlaysModule'])

        .controller('AdminFilmsEditController', ['$routeParams', '$location', 'Films', function($routeParams, $location, Films) {

            var ctrl = this;
            this.currentFilm = {};

            Films.get({id:$routeParams.fid}).$promise.then(function (data) {
                ctrl.currentFilm = data;
            }, function () {
                $location.path("/admin/films");
            });


            this.save = function () {
                Films.update({id: ctrl.currentFilm.fid}, ctrl.currentFilm).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                    $location.path("/admin/films")
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };


        }])
        .controller('AdminFilmsController', ['$location', 'Films', function ($location, Films) {


            var ctrl = this;
            this.newFilm = new Films();


            var init = function () {

                ctrl.loading = true;
                ctrl.films = [];
            };

            this.loadFilms = function () {

                init();

                Films.query(function (data) {
                    ctrl.films = data;
                    ctrl.loading = false;
                }, function (){
                    //Fail Case
                });
            };

            this.addFilm = function () {

                ctrl.newFilm.$save( function (data) {
                    ctrl.films.push(data);
                    ctrl.newFilm = new Films();
                    console.log("Film insertion success");
                    $location.path("/admin/films");

                }, function () {
                    //Fail Case
                    console.log("Film insertion fail");
                });
            };

            this.deleteFilm = function (film) {
                Films.delete({fid: film.fid}, function (){
                    console.log("Film deletion success");
                    ctrl.users.splice(ctrl.users.indexOf(user), 1);
                }, function () {
                    console.log("Film deletion fail");
                });

            };

            this.open_delete_modal = function (index) {
                $('#modal_deleteAgree').openModal();
                this.tmpFilm = this.films[index];
            };

            this.loadFilms();

        }]);


})();
