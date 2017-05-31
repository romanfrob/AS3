

'use strict';

angular.module('clinicApp.employee', [])
    .controller('EmployeeCtrl', function ($scope, $q, requestFactory, $rootScope, $location, AuthenticationService)
    {
        $scope.clients = [];
        $scope.selectedClient = {};
        $scope.consults = [];
        $scope.selectedConsult = {};
        $scope.selectedConsult.doctorID = "0";
        $scope.doctors = [];
        $scope.selectedDoctorID = 0;
        var employeeID = $rootScope.globals.currentUser.userID;
        var config = {headers: {'Content-Type': 'application/json'}};

        $scope.getClients = function () {
            requestFactory.getAllPatients()
                .then(
                    function (response) {
                        $scope.clients = response.data;
                    },
                    function(errResponse, status){
                        alert("Error while retrieving clients: " + status);
                        return $q.reject(errResponse);
                    }
                );
        };

        $scope.getClients();

        $scope.showClient = function (client) {
            $scope.clearConsult();
            $scope.selectedClient = client;
            $scope.selectedClient.birthDate = new Date($scope.selectedClient.birthDate);
        };

        $scope.clearForm = function () {
            $scope.selectedClient = {};
            $scope.consults = [];
        };

        $scope.getAccounts = function (cnp) {
            requestFactory.getPatientConsults(cnp)
                .then(
                    function (response) {
                        $scope.consults = response.data;
                    },
                    function(errResponse, status){
                        alert("Error while retrieving patient consults: " + status);
                        return $q.reject(errResponse);
                    }
                );
        };

        $scope.convertToDate = function (timestamp) {
            return moment(timestamp).format("DD.MM.YYYY  hh:mm");
        };

        $scope.showConsult = function (consult) {
            //$scope.selectedConsult = consult;
            $scope.selectedConsult.since = new Date(consult.since);
            $scope.selectedConsult.till = new Date(consult.till);
            $scope.selectedConsult.status = consult.status;
            $scope.selectedConsult.cnp = $scope.selectedClient.cnp;
            $scope.selectedConsult.consultID = consult.consultID;
            $scope.selectedConsult.doctorID = consult.doctorID;
            $scope.selectedDoctorID = $scope.doctors.filter(function(d) { return d.doctorId === consult.doctorID; })[0];
            $scope.selectedDoctorID.doctorID = $scope.selectedDoctorID.doctorId;
        };

        $scope.clearConsult = function () {
            $scope.selectedConsult = {};
        };

        $scope.insertClient = function () {
            if(validCNP($scope.selectedClient.cnp)) {
                var client = {};
                client['name'] = $scope.selectedClient.name;
                client['cnp'] = $scope.selectedClient.cnp;
                client['address'] = $scope.selectedClient.address;
                client['birthDate'] = $scope.selectedClient.birthDate.getTime();

                requestFactory.createPatient(client, config)
                    .then(
                        function () {
                            $scope.getClients();
                            $scope.selectedClient = {};
                        },
                        function () {
                            alert("Duplicate CNP: " + status);
                        });
            } else {
                alert("Invalid CNP: " + status);
            }
        };

        $scope.updateClient = function () {
            if(validCNP($scope.selectedClient.cnp)) {
                var client = {};
                client['name'] = $scope.selectedClient.name;
                client['cnp'] = $scope.selectedClient.cnp;
                client['address'] = $scope.selectedClient.address;
                client['birthDate'] = $scope.selectedClient.birthDate.getTime();

                requestFactory.updatePatient(client, config)
                    .then(
                        function () {
                            $scope.getClients();
                            $scope.selectedClient = {};
                        },
                        function ()
                        {
                            alert("Update failed: " + status);
                        });
            }
            else {
                alert("New CNP not valid");
            }
        };
        
        $scope.getAvailableDoctors = function (time) {
            requestFactory.getAvailableDoctors(Math.floor(time/1000))
                .then(
                    function (response) {
                        $scope.doctors = response.data;
                    },
                    function () {
                        
                    }
                );
        };

        $scope.getAvailableDoctors(Date.now());

        $scope.insertAccount = function () {
            var account = {};
            account['cnp'] = $scope.selectedConsult.cnp;
            account['consultID'] = "0";
            account['doctorID'] = $scope.selectedDoctorID.doctorId;
            account['since'] = $scope.selectedConsult.since.getTime();
            account['till'] = $scope.selectedConsult.till.getTime();

            requestFactory.createConsult(account, config)
                .then(
                    function () {
                        $scope.getClients();
                        $scope.clearConsult();
                    },
                    function ()
                    {
                        alert("Operation Failed: " + status);
                    });
        };

        $scope.checkIn = function () {
            var account = {};
            account['cnp'] = $scope.selectedConsult.cnp;
            account['consultID'] = "0";
            account['doctorID'] = $scope.selectedDoctorID.doctorId;
            account['since'] = $scope.selectedConsult.since.getTime();
            account['till'] = $scope.selectedConsult.till.getTime();

            if($scope.selectedConsult.consultID) {
                account['consultID'] = $scope.selectedConsult.consultID;
            }

            requestFactory.checkIn(account, config)
                .then(
                    function () {
                        // $scope.selectedConsult.status = "ongoing";
                        // $scope.consults.filter(function (c) {
                        //     return c.consultID === $scope.selectedConsult.consultID;
                        // })[0] = "ongoing";
                        $scope.getAccounts($scope.selectedConsult.cnp);
                    },
                    function ()
                    {
                        alert("Operation Failed: " + status);
                    });
        };

        $scope.checkOut = function () {
            var account = {};
            account['cnp'] = $scope.selectedConsult.cnp;
            account['doctorID'] = $scope.selectedDoctorID.doctorID;
            account['since'] = $scope.selectedConsult.since.getTime();
            account['till'] = $scope.selectedConsult.till.getTime();
            account['consultID'] = $scope.selectedConsult.consultID;
            requestFactory.checkOut(account, employeeID)
                .then(
                    function () {
                        $scope.getAccounts($scope.selectedConsult.cnp);
                    },
                    function () {
                        alert("Failed to checkout");
                    }
                )
        };


        function validCNP( p_cnp ) {
            var i, year, hashResult = 0, cnp = [], hashTable = [2,7,9,1,4,6,3,5,8,2,7,9];

            if( p_cnp.length !== 13 ) {
                return false;
            }

            for( i=0 ; i<13 ; i++ ) {
                cnp[i] = parseInt( p_cnp.charAt(i) , 10 );
                if( isNaN( cnp[i] ) ) {
                    return false;
                }
                if( i < 12 ) {
                    hashResult = hashResult + ( cnp[i] * hashTable[i] );
                }
            }
            hashResult = hashResult % 11;
            if( hashResult === 10 ) {
                hashResult = 1;
            }
            year = (cnp[1]*10) + cnp[2];
            switch( cnp[0] ) {
                case 1  : case 2 : { year += 1900; } break;
                case 3  : case 4 : { year += 1800; } break;
                case 5  : case 6 : { year += 2000; } break;
                case 7  : case 8 : case 9 : { year += 2000; if( year > ( parseInt( new Date().getYear() , 10 ) - 14 ) ) { year -= 100; } } break;
                default : { return false; }
            }

            if( year < 1800 || year > 2099 ) {
                return false;
            }
            return ( cnp[12] === hashResult );
        }

        $scope.logout = function () {
            $location.path('/');
            AuthenticationService.ClearCredentials();
        }
    });