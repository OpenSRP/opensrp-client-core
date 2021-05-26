describe("FP E2E", function(){
    beforeEach(function(){
        browser().navigateTo('../assets/www/smart_registry/fp_register.html');
    });

    describe("Status Flags", function(){
        it("should show the fp flag only if client is high priority", function(){
            expect(element("div.client-details:nth(0) .flag-hp:visible").count()).toEqual(1);
            expect(element("div.client-details:nth(1) .flag-hp:hidden").count()).toEqual(1);
        });

        it("should show the bpl flag only if economic status is bpl", function(){
            expect(element("div.client-details:nth(0) .flag-bpl:visible").count()).toEqual(1);
            expect(element("div.client-details:nth(1) .flag-bpl:hidden").count()).toEqual(1);
        });

        it("should include the sc flag element only if caste is sc", function(){
            expect(element("div.client-details:nth(0) .flag-sc").count()).toEqual(1);
            expect(element("div.client-details:nth(0) .flag-st").count()).toEqual(0);
        });

        it("should include the st flag element only if caste is st", function(){
            expect(element("div.client-details:nth(1) .flag-sc").count()).toEqual(0);
            expect(element("div.client-details:nth(1) .flag-st").count()).toEqual(1);
        });
    });
});