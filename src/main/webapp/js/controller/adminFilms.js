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
        .controller('AdminFilmsController', ['$rootScope', '$location', 'Films', function ($rootScope, $location, Films) {


            var ctrl = this;
            this.newFilm = new Films();


            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

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

            this.deleteFilm = function () {
                Films.delete({id: ctrl.tmpFilm.fid}, function (){
                    console.log("Film deletion success");
                }, function () {
                    console.log("Film deletion fail");
                });

            };

            this.open_modal = function (index) {
                this.tmpFilm = this.films[index];
                $('#modal_film_delete').openModal();
            };

            this.close_modal = function () {
                $('#modal_film_delete').closeModal();
            };

            this.loadFilms();

        }]);


})();
