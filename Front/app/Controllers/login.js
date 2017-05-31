
'use strict';

angular.module('clinicApp.login', [])
    .controller('LoginCtrl', function($scope, $location, AuthenticationService)
    {
        $scope.username = "";
        $scope.password = "";
        $scope.dataLoading = false;
        $scope.userType = "";

        AuthenticationService.ClearCredentials();

        $scope.login = function ()
        {
            $scope.dataLoading = true;
            AuthenticationService.Login($scope.username, $scope.password, function (response)
            {
                if (response)
                {
                    var doctorID = 0;
                    switch(response.data.userType){
                        case "admin":
                            $scope.userType = "manager";
                            break;
                        case "doctor":
                            $scope.userType = "doctor";
                            doctorID = response.data.doctorID;
                            break;
                        case "secretary":
                            $scope.userType = "employee";
                            break;
                        default:
                            $scope.userType = "employee";
                    }

                    AuthenticationService.SetCredentials($scope.username, $scope.password, $scope.userType, doctorID);
                    $location.path('/' + $scope.userType);
                } else {
                    $scope.dataLoading = false;
                }
            });
        }
    });