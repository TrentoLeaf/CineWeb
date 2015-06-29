(function () {
    'use strict';

    angular.module('adminPlays', ['filmsPlaysModule'])

        .controller('AdminPlaysEditController', ['$routeParams', '$location', 'Plays', '$rootScope', function($routeParams, $location, Plays, $rootScope) {

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
                    ctrl.updatePlays();
                    $location.path("/admin/plays")
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };

            this.updatePlays = function () {
                $rootScope.loadPlaysByDate();
            };

        }])
        .controller('AdminPlaysController', ['$rootScope', '$location', 'Plays', 'Rooms', function ($rootScope, $location, Plays, Rooms) {


            var ctrl = this;
            this.newPlay = new Plays();
            this.currentPlay = {};
            this.newPlay_3d = false;
            this.shared_obj = {};

            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                ctrl.loading = true;
                ctrl.currentPlay = {};
            };

            this.loadPlay = function () {

                init();

            };

            this.setCurrentPlay = function (indexDate, indexFilm, indexPlay) {

                ctrl.currentPlay = ctrl.playGenerator(indexDate, indexFilm, indexPlay);

                // TODO rimuovere il commento qui sotto
                //ctrl.tmpPlay = ctrl.plays[index];
                // TODO recuperare la matrice della sala e metterla in ctrl.tmpPlay.theatreMap
                //ctrl.tmpPlay.theatreMap = undefined;

                Rooms.getRoomByID(ctrl.currentPlay.rid)
                    .success(function (data) {
                        ctrl.shared_obj.mapTheatre = data.seats;
                    })
                    .error(function () {
                        ctrl.shared_obj.mapTheatre = [];
                    });

                // TODO selezionare la proiezione ($(elemento-giusto).addClass('admin-elem-active');), e togliere la vecchia selezione
            };

            this.addPlay = function (data) {
                if (ctrl.newPlay_3d) {
                    ctrl.newPlay._3d = true;
                } else {
                    ctrl.newPlay._3d = false;
                }

                ctrl.newPlay.$save( function (data) {
                    ctrl.plays.push(data);
                    ctrl.newPlay = new Films();
                    console.log("Play insertion success");
                    ctrl.updatePlays();
                    $location.path("/admin/plays");

                }, function () {
                    //Fail Case
                    console.log("Play insertion fail");
                });
            };

            this.open_delete_modal = function (indexDate, indexFilm, indexPlay) {
                $('#modal_deleteAgree').openModal();
                ctrl.setCurrentPlay(indexDate, indexFilm, indexPlay);
            };

            this.open_modify_page = function (indexDate, indexFilm, indexPlay) {
                // TODO change modal
                $('#modal_deleteAgree').openModal();
                ctrl.setCurrentPlay(indexDate, indexFilm, indexPlay);
                // TODO chiaramente non funziona --> chiedere a Sam fare merge tra il controller sopra e questo
                $location.path('/admin/plays/' + ctrl.currentPlay.pid);
            };

            this.playGenerator = function (indexDate, indexFilm, indexPlay) {
                var tmpFilm = ctrl.cloneObject($rootScope.playsByDate[indexDate].films[indexFilm]);
                tmpFilm.pid = tmpFilm.plays[indexPlay].pid;
                tmpFilm.rid = tmpFilm.plays[indexPlay].rid;
                tmpFilm.free = tmpFilm.plays[indexPlay].free;
                tmpFilm.date = tmpFilm.plays[indexPlay].date;
                tmpFilm.time = tmpFilm.plays[indexPlay].time;
                delete tmpFilm.plays;
                return tmpFilm;
            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

            this.updatePlays = function () {
                $rootScope.loadPlaysByDate();
            };

            this.loadPlay();
        }]);


})();