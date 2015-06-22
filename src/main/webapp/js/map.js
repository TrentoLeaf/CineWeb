(function () {
    'use strict';

    angular.module('mapModule', [])
        .directive('map', function () {
            return {
                restrict: 'E',
                template: '<div><p>{{o | json}}</p><button ng-click="c.a()">EEE</button></div>',
                scope: {
                    o: '=o'
                },
                controller: ['$scope', function ($scope) {
                    this.a = function () {
                        $scope.o.e = "eeeeeeeee";
                        $scope.o.seats = $scope.o.seats || [];
                        $scope.o.seats.push(3);
                    }
                }],
                controllerAs: 'c',
                link: function (scope, element, attrs) {
                    scope.$watch('o.seats', function (seats) {
                        // seats == scope.o.seats
                        console.log(seats);
                        console.log(scope.seats);

                        scope.o.f = scope.o.f || [];
                        scope.o.f.push(1);

                    }, true);
                }
            }
        })

        .controller('MapController', function () {
            this.test = {
                a: 123,
                b: "pippo",
                c: [1, 4, 5]
            };

            this.do = function () {
                this.test.b = "pluto";
                this.test.d = this.test.d || [];
                this.test.d.push(21);
            };
        });

})();
