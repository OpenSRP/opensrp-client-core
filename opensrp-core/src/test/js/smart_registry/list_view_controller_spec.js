describe("List view controller:", function () {
    var controller, scope, bridge = new FPRegistryBridge();

    beforeEach(module("smartRegistry.controllers"));
    beforeEach(module("smartRegistry.filters"));
    beforeEach(module("smartRegistry.services"));
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        scope.bridge = bridge;
        scope.clients = [
            {name: "name 1"},
            {name: "name 2"}
        ];
        scope.villageFilterOption = {};
        scope.serviceModeOption = {};
        scope.defaultVillageOptions = {
            type: "filterVillage",
            options: [
                {
                    label: "All",
                    id: "",
                    handler: ""
                }
            ]
        };
        scope.defaultVillageFilterHandler = "defaultFilterByVillageName";
        controller = $controller("listViewController", {
            $scope: scope
        });
    }));

    describe("Initialisation:", function () {
        it("should default page size and current page.", function () {
            expect(scope.currentPage).toBe(0);
            expect(scope.pageSize).toBe(20);
        });
    });

    describe("Reporting period:", function () {
        it("should start on the 26th of the previous month if the specified day is on or before the 25th", function () {
            var date_str = "2012-02-25";
            expect(scope.getReportPeriodStartDate(date_str)).toEqual("26/01");
        });

        it("should end on the 25th of the current month if the day is on or before the 25th", function () {
            var date_str = "2012-02-25";
            expect(scope.getReportingPeriodEnd(date_str)).toEqual("25/02");
        });

        it("should start on the 26th of the current month if the specified day is after the 25th", function () {
            var date_str = "2012-02-26";
            expect(scope.getReportPeriodStartDate(date_str)).toEqual("26/02");
        });

        it("should end on the 25th of the next month if the day is after the 25th", function () {
            var date_str = "2012-02-26";
            expect(scope.getReportingPeriodEnd(date_str)).toEqual("25/03");
        });

        it("should end in Jan of the next year if the next month is january", function () {
            var date_str = "2012-12-26";
            expect(scope.getReportingPeriodEnd(date_str)).toEqual("25/01");
        });
    });

    describe("sort", function () {
        it("should set currentSortOption to the selected one.", function () {
            var sortOption = {
                label: "Name (A to Z)",
                handler: "sortByName"
            };
            scope.sort(sortOption);

            expect(scope.currentSortOption).toBe(sortOption);
        });

        it("should set sort list handler based on the selected sort option along with name field as secondary sort.", function () {
            var sortOption = {
                label: "Name (A to Z)",
                handler: "sortByName"
            };
            scope.sort(sortOption);

            expect(scope.sortList).toEqual([scope.sortByName, "name"]);
        });

        it("should set sort list handler based on the selected sort option along with specific secondary sort field.", function () {
            var sortOption = {
                label: "Name (A to Z)",
                handler: "sortByName",
                secondarySortKey: "age"
            };
            scope.sort(sortOption);

            expect(scope.sortList).toEqual([scope.sortByName, "age"]);
        });

        it("should set sort order based on the selected sort option.", function () {
            var descendingSortOption = {
                label: "Name (A to Z)",
                handler: "sortByPriority",
                sortDescending: true
            };
            var ascendingSortOption = {
                label: "HP",
                handler: "sortByName",
                sortDescending: false
            };
            scope.sort(descendingSortOption);
            expect(scope.sortDescending).toBeTruthy();

            scope.sort(ascendingSortOption);
            expect(scope.sortDescending).toBeFalsy();
        });
    });

    describe("sortByName:", function () {
        it("should sort by name field.", function () {
            expect(scope.sortByName({name: "name1"})).toBe("name1");
        });
    });

    describe("sortByPriority:", function () {
        it("should sort clients by high priority.", function () {
            var client = {
                name: "client1",
                isHighPriority: true
            };

            var sort = scope.sortByPriority(client);

            expect(sort).toBeFalsy();

            sort = scope.sortByPriority({
                name: "client1",
                isHighPriority: false
            });

            expect(sort).toBeTruthy();
        });
    });

    describe("filterVillage:", function () {
        it("should set villageFilterOption to selected one.", function () {
            var option = {};

            scope.filterVillage(option);

            expect(scope.villageFilterOption).toBe(option);
        });
    });

    describe("filterList:", function () {
        it("should allow a client when it passes applied search criteria.", function () {
            scope.searchCriteria = function () {
                return true;
            };
            scope.searchFilterString = "foo";
            scope.villageFilterOption = {};
            scope.serviceModeOption = {};
            var client = {};

            expect(scope.filterList(client)).toBeTruthy();
        });

        it("should filter client when it does not pass applied search criteria.", function () {
            scope.searchCriteria = function () {
                return false;
            };
            scope.searchFilterString = "foo";
            scope.villageFilterOption = {};
            scope.serviceModeOption = {};
            var client = {};

            expect(scope.filterList(client)).toBeFalsy();
        });

        it("should allow a client when it passes applied village filter.", function () {
            scope.villageFilterHandler = function () {
                return true;
            };
            scope.villageFilterOption = {handler: "villageFilterHandler"};
            scope.searchFilterString = null;
            scope.serviceModeOption = {};
            var client = {};

            expect(scope.filterList(client)).toBeTruthy();
        });

        it("should filter client when it does not pass applied village filter.", function () {
            scope.villageFilterHandler = function () {
                return false;
            };
            scope.villageFilterOption = {handler: "villageFilterHandler"};
            scope.searchFilterString = null;
            scope.serviceModeOption = {};
            var client = {};

            expect(scope.filterList(client)).toBeFalsy();
        });

        it("should allow a client when it passes applied service mode filter.", function () {
            scope.serviceModeHandler = function () {
                return true;
            };
            scope.villageFilterOption = {};
            scope.searchFilterString = null;
            scope.serviceModeOption = {handler: "serviceModeHandler"};
            var client = {};

            expect(scope.filterList(client)).toBeTruthy();
        });

        it("should filter client when it does not pass applied village filter.", function () {
            scope.serviceModeHandler = function () {
                return false;
            };
            scope.villageFilterOption = {};
            scope.searchFilterString = null;
            scope.serviceModeOption = {handler: "serviceModeHandler"};
            var client = {};

            expect(scope.filterList(client)).toBeFalsy();
        });

        it("should allow only those clients that pass all the applied filters.", function () {
            scope.searchCriteria = function (client, searchCriteria) {
                return client.name === searchCriteria;
            };
            scope.villageFilterHandler = function () {
                return true;
            };
            scope.serviceModeHandler = function () {
                return true;
            };
            scope.searchFilterString = "foo";
            scope.villageFilterOption = {handler: "villageFilterHandler"};
            scope.serviceModeOption = {handler: "serviceModeHandler"};
            var client1 = {name: "foo"};
            var client2 = {name: "not foo"};

            expect(scope.filterList(client1)).toBeTruthy();
            expect(scope.filterList(client2)).toBeFalsy();
        });
    });

    describe("addVillageNamesToFilterOptions:", function () {
        it("should add filter options for every village.", function () {
            var expectedVillageOptions = {
                type: "filterVillage",
                options: [
                    {
                        label: "All",
                        id: "",
                        handler: ""
                    },
                    {
                        label: "Village 1",
                        id: "village_1",
                        handler: "defaultFilterByVillageName"
                    },
                    {
                        label: "Village2",
                        id: "village2",
                        handler: "defaultFilterByVillageName"
                    }
                ]
            };
            scope.villageBridge = new VillageBridge();
            spyOn(scope.villageBridge, "getVillages").andReturn([
                {name: "village_1"},
                {name: "village2"}
            ]);

            var villageFilterOptions = scope.getVillageFilterOptions();
            expect(villageFilterOptions).toEqual(expectedVillageOptions);
        });
    });

    describe("search:", function () {
        it("should not be in search mode when user has not tapped on search box", function () {
            expect(scope.inSearchMode).toBeFalsy();
        });

        it("should be in search mode when user taps on search box", function () {
            scope.enterSearchMode();

            expect(scope.inSearchMode).toBeTruthy();
        });

        it("should clear search string when search is cancelled", function () {
            scope.enterSearchMode();
            scope.searchFilterString = "criteria";

            scope.cancelSearch();

            expect(scope.searchFilterString).toBe("");
            expect(scope.inSearchMode).toBeFalsy();
        });

        it("should enter search mode", function () {
            scope.enterSearchMode();

            expect(scope.inSearchMode).toBeTruthy();
        });
    });

    describe("sort by BPL:", function () {
        it("should sort clients by BPL.", inject(function ($filter) {
            var clients = [
                {
                    name: "client1",
                    economicStatus: "",
                    isBPL: "" && ("".toUpperCase() == "BPL")
                },
                {
                    name: "client2",
                    economicStatus: "bpl",
                    isBPL: "bpl" && ("bpl".toUpperCase() == "BPL")
                }
            ];

            var filterFunc = scope.sortByBPL;
            var filteredClients = $filter("orderBy")(clients, filterFunc);
            expect(filteredClients[0]).toEqual(clients[1]);
            expect(filteredClients[1]).toEqual(clients[0]);
        }));
    });

    describe("sort by SC:", function () {
        it("should sort clients by SC.", inject(function ($filter) {
            var clients = [
                {
                    name: "client1",
                    caste: "st",
                    isSC: "st" && "st".toUpperCase() === "SC"
                },
                {
                    name: "client2",
                    caste: "sc",
                    isSC: "sc" && "sc".toUpperCase() === "SC"
                }
            ];

            var filterFunc = scope.sortBySC;
            var filteredClients = $filter("orderBy")(clients, filterFunc);
            expect(filteredClients[0]).toEqual(clients[1]);
            expect(filteredClients[1]).toEqual(clients[0]);
        }));
    });

    describe("sort by ST:", function () {
        it("should sort clients by ST.", inject(function ($filter) {
            var clients = [
                {
                    name: "client1",
                    caste: "sc",
                    isST: "sc" && "sc".toUpperCase() === "ST"
                },
                {
                    name: "client2",
                    caste: "st",
                    isST: "st" && "st".toUpperCase() === "ST"
                }
            ];

            var filterFunc = scope.sortByST;
            var filteredClients = $filter("orderBy")(clients, filterFunc);
            expect(filteredClients[0]).toEqual(clients[1]);
            expect(filteredClients[1]).toEqual(clients[0]);
        }));
    });
});
