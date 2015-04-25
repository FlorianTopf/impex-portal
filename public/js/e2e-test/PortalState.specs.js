'use strict';

/* https://github.com/angular/protractor/blob/master/docs/toc.md */

describe('portal', function() {

  describe('initial view', function() {

    browser.get('/');
    
    it('should automatically redirect to portal when location hash/fragment is empty', function() {
        expect(browser.getLocationAbsUrl()).toMatch("/portal");
    });

    it('should render initial view', function() {
      var databases = element.all(by.repeater('database in vm.configService.config.databases'));
      var services = element.all(by.repeater('service in vm.configService.config.databases'));
      expect(databases.count()).toMatch(5);
      expect(services.count()).toMatch(5);
      expect(element.all(by.id('MY-DATA')).count()).toMatch(1);
      expect(element.all())
    });

  });


  /*describe('view2', function() {

    beforeEach(function() {
      browser.get('index.html#/view2');
    });


    it('should render view2 when user navigates to /view2', function() {
      expect(element.all(by.css('[ng-view] p')).first().getText()).
        toMatch(/partial for view 2/);
    });

  }); */
});