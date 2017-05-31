
'use strict';

angular.module('clinicApp.doctor', ['ngWebSocket'])
    // .factory('wsFactory', function ($websocket) {
    //     return $websocket("ws://localhost:8080/Clinic/notification");
    // })
    .controller('DoctorCtrl', function ($scope, $q, requestFactory, $rootScope, $interval, $location, AuthenticationService)
    {
        $scope.patient = {};
        var config = {headers: {'Content-Type': 'application/json'}};

        // wsFactory.onMessage(function (patient) {
        //     $scope.patient.patientName = JSON.stringify(patient);
        // })

        var updatePatient = function () {
            requestFactory.notificationForDoctor($rootScope.globals.currentUser.doctorID)
                .then(function (response) {
                    $scope.patient = response.data;
                });
        };

        $interval(updatePatient, 2000);

        $scope.logout = function () {
            $location.path('/');
            AuthenticationService.ClearCredentials();
        };

        $scope.updateDescription = function () {
            var consult = {};
            consult.patientID = $scope.patient.patientID;
            consult.description = $scope.patient.description;
            consult.doctorID = $scope.patient.doctorID;
            requestFactory.updateConsult(consult, config);
        }
    });