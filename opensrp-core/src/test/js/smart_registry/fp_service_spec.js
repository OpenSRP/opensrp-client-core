describe("FP Service:", function () {

    var fpSrvc;

    beforeEach(module("smartRegistry.services"));
    beforeEach(module("smartRegistry.filters"));
    beforeEach(inject(function (FPService) {
        fpSrvc = FPService;
    }));

    describe("Pre-process Clients:", function () {
        it("should set referral data if a referral alert exists", function () {
            var clients = [
                {
                    fp_method:"female_sterilization",
                    family_planning_method_change_date:"2013-02-25",
                    alerts:[
                        {
                            name:"OCP Refill",
                            date:"2012-07-24",
                            status:"urgent"
                        },
                        {
                            name:"Female Sterilization Followup 1",
                            date:"2012-05-18",
                            status:"urgent"
                        },
                        {
                            name:"FP Referral Followup",
                            date:"2012-05-18",
                            status:"normal"
                        }
                    ]
                }
            ];

            var expectedOutput =
            {
                name: "FP Referral Followup",
                alert_index: 2,
                type: "referral"
            };
            expect(fpSrvc.preProcessClients(clients)[0].refill_follow_ups).toEqual(expectedOutput);
        });

        it("should set fp followup data if a fp followup exists and referral alert does not exist", function () {
            var clients = [
                {
                    fp_method:"female_sterilization",
                    family_planning_method_change_date:"2013-02-25",
                    alerts:[
                        {
                            name:"OCP Refill",
                            date:"2012-07-24",
                            status:"urgent"
                        },
                        {
                            name:"Female Sterilization Followup 1",
                            date:"2012-05-18",
                            status:"urgent"
                        },
                        {
                            name:"FP Followup",
                            date:"2012-05-12",
                            status:"normal"
                        }
                    ]
                }
            ];

            var expectedOutput =
            {
                name: "FP Followup",
                alert_index: 2,
                type: "follow-up"
            };
            expect(fpSrvc.preProcessClients(clients)[0].refill_follow_ups).toEqual(expectedOutput);
        });

        it("should set female sterilization follow-up data when a female sterilization alert exits, and referral data is NOT specified", function () {
            var clients = [
                {
                    fp_method:"female_sterilization",
                    family_planning_method_change_date:"2013-02-25",
                    alerts:[
                        {
                            name:"OCP Refill",
                            date:"2013-07-24",
                            status:"urgent"
                        },
                        {
                            name:"Female sterilization Followup 1",
                            date:"2012-05-18",
                            status:"urgent"
                        }
                    ]
                }
            ];

            var expectedOutput =
            {
                name: "Female sterilization Followup 1",
                alert_index: 1,
                type: "follow-up"
            };
            expect(fpSrvc.preProcessClients(clients)[0].refill_follow_ups).toEqual(expectedOutput);
        });

        it("should only set female sterilization follow-up data when fp method is also female sterilization", function () {
            var clients = [
                {
                    fp_method:"female_sterilization",
                    family_planning_method_change_date:"2013-02-25",
                    alerts:[
                        {
                            name:"Male Sterilization Followup",
                            date:"2012-05-18",
                            status:"urgent"
                        },
                        {
                            name:"Female sterilization Followup 1",
                            date:"2012-05-18",
                            status:"urgent"
                        }
                    ]
                }
            ];

            var expectedOutput =
            {
                name: "Female sterilization Followup 1",
                alert_index: 1,
                type: "follow-up"
            };
            expect(fpSrvc.preProcessClients(clients)[0].refill_follow_ups).toEqual(expectedOutput);
        });

        it("should set condom refill data only if fp_method is also condom", function () {
            var clients = [
                {
                    fp_method:"condom",
                    family_planning_method_change_date:"2013-02-25",
                    alerts:[
                        {
                            name:"OCP Refill",
                            date:"2012-05-18",
                            status:"urgent"
                        },
                        {
                            name:"Condom Refill",
                            date:"2012-05-18",
                            status:"urgent"
                        }
                    ]
                }
            ];

            var expectedOutput =
            {
                name: "Condom Refill",
                alert_index: 1,
                type: "refill"
            };
            expect(fpSrvc.preProcessClients(clients)[0].refill_follow_ups).toEqual(expectedOutput);
        });

        it("should set condom refill date over sterilisation data if fp_method is condom and NOT *_sterilization", function(){
            var clients = [
                {
                    fp_method:"condom",
                    family_planning_method_change_date:"2013-02-25",
                    alerts:[
                        {
                            name:"Female sterilization Followup 1",
                            date:"2012-05-18",
                            status:"urgent"
                        },
                        {
                            name:"Condom Refill",
                            date:"2012-05-18",
                            status:"urgent"
                        }
                    ]
                }
            ];

            var expectedOutput =
            {
                name: "Condom Refill",
                alert_index: 1,
                type: "refill"
            };
            expect(fpSrvc.preProcessClients(clients)[0].refill_follow_ups).toEqual(expectedOutput);
        });
    });

});
