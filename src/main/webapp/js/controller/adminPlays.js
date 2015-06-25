(function () {
    'use strict';

    angular.module('adminPlays', ['filmsPlaysModule'])
        .controller('AdminPlaysController', ['$location', 'Plays', function ($location, Plays) {


            var ctrl = this;
            this.newPlay = new Plays();


            var init = function () {

                ctrl.loading = true;
                ctrl.plays = [];
            };

            this.loadPlay = function () {

                init();

                Films.query(function (data) {
                    ctrl.plays = data;
                    ctrl.loading = false;
                }, function (){
                    //Fail Case
                });
            };

            this.addPlay = function (data) {

                ctrl.newPlay.$save( function (data) {
                    ctrl.films.push(data);
                    ctrl.newFilm = new Films();
                    console.log("Film insertion success");
                    $location.path("/admin/films");

                }, function () {
                    //Fail Case
                    console.log("Film insertion fail");
                });
            };

            this.loadFilms();

        }]);


})();