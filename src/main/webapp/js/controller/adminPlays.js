(function () {
    'use strict';

    angular.module('adminPlays', ['filmsPlaysModule'])
        .controller ('AdminNewPlaysController', ['Films', '$rootScope', '$location', 'Plays', function (Films, $rootScope, $location, Plays) {

        var ctrl = this;
        this.newPlay =  new Plays();
        this.newPlay_3d = false;
        this.films = [];


        this.loadFilms = function () {

            Films.query(function (data) {
                ctrl.films = data;
                console.log(ctrl.films);
            }, function (){
                //Fail Case
            });
        };

        this.addPlay = function (data) {
            if (ctrl.newPlay_3d) {
                ctrl.newPlay._3d = true;
            } else {
                ctrl.newPlay._3d = false;
            }

            ctrl.newPlay.$save(function (data) {
                ctrl.newPlay = new Plays();
                console.log("Play insertion success");
                ctrl.updatePlays();
                $location.path("/admin/plays");

            }, function () {
                //Fail Case
                console.log("Play insertion fail");
            });
        };

        this.updatePlays = function () {
            $rootScope.loadPlaysByDate();
        };

        this.loadFilms();
    }])
        .controller('AdminPlaysController', ['$rootScope', '$location', 'Plays', 'Rooms', 'Films', function ($rootScope, $location, Plays, Rooms, Films) {


            var ctrl = this;
            this.currentPlay = {};
            this.currentSelectedDate = -1;
            this.currentSelectedFilm = -1;
            this.currentSelectedPlay = -1;
            this.shared_obj = {};
            this.films = [];

            this.init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                ctrl.loading = true;
                ctrl.currentPlay = {};
                ctrl.loadFilms();
            };

            this.loadFilms = function () {

                Films.query(function (data) {
                    ctrl.films = data;
                    console.log(ctrl.films);
                }, function (){
                    //Fail Case
                });
            };


            this.setCurrentPlay = function (indexDate, indexFilm, indexPlay) {

                ctrl.currentPlay = ctrl.playGenerator(indexDate, indexFilm, indexPlay);
                ctrl.currentSelectedDate = indexDate;
                ctrl.currentSelectedFilm = indexFilm;
                ctrl.currentSelectedPlay = indexPlay;

                Rooms.getRoomStatus(ctrl.currentPlay.rid)
                    .success(function (data) {
                        ctrl.shared_obj.editable = false;
                        ctrl.shared_obj.mapTheatre = data.seats;
                    })
                    .error(function () {
                        ctrl.shared_obj.editable = false;
                        ctrl.shared_obj.mapTheatre = [];
                    });
            };

            this.deletePlay = function () {
                Plays.delete({id: ctrl.currentPlay.pid}, function () {
                    console.log("Play deletion success");
                    ctrl.updatePlays();
                }, function () {
                    console.log("Play deletion fail");
                });
            };

            this.updatePlays = function () {
                $rootScope.loadPlaysByDate();
            };


            this.open_delete_modal = function (indexDate, indexFilm, indexPlay) {
                ctrl.setCurrentPlay(indexDate, indexFilm, indexPlay);
                $('#modal_deleteAgree').openModal();
            };
            this.close_delete_modal = function () {
                $('#modal_deleteAgree').closeModal();
            };

            this.playGenerator = function (indexDate, indexFilm, indexPlay) {
                var tmpFilm = ctrl.cloneObject($rootScope.playsByDate[indexDate].films[indexFilm]);
                tmpFilm.pid = tmpFilm.plays[indexPlay].pid;
                tmpFilm.rid = tmpFilm.plays[indexPlay].rid;
                tmpFilm.free = tmpFilm.plays[indexPlay].free;
                tmpFilm.time = new Date(tmpFilm.plays[indexPlay].time);
                delete tmpFilm.date;
                delete tmpFilm.plays;
                return tmpFilm;
            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

            this.updatePlays = function () {
                $rootScope.loadPlaysByDate();
            };

            this.init();
        }]);


})();