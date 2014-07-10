'use strict'

describe('ConfigCtrl', function() {
	var scope, cService, uService, state, cfg;
	
	beforeEach(angular.mock.module('portal'));
	
	
	// @TODO what about $state?
	// @TODO use httpbackend for simulation config 
	beforeEach(angular.mock.inject(function($rootScope, configService, userService, $controller, $state) {

		scope = $rootScope.$new();
		cService = configService;
		uService = userService;
		state = $state
		cfg = {
				"databases":[
		            {"id":"spase://IMPEX/Repository/FMI/HYB","type":"simulation","name":"FMI-HYBRID","description":"FMI Hybrid web archive","dns":["impex-fp7.fmi.fi"],"methods":["/ws/Methods_FMI.wsdl"],"tree":["/ws/Tree_FMI_HYB.xml"],"protocol":["http"],"info":"http://hwa.fmi.fi/beta/login.php"},
		            {"id":"spase://IMPEX/Repository/FMI/GUMICS","type":"simulation","name":"FMI-GUMICS","description":"FMI MHD web archive","dns":["impex-fp7.fmi.fi"],"methods":["/ws/Methods_FMI.wsdl"],"tree":["/ws/Tree_FMI_GUMICS.xml"],"protocol":["http"],"info":"http://hwa.fmi.fi/beta/login.php"},
		            {"id":"spase://IMPEX/Repository/LATMOS","type":"simulation","name":"LATMOS","description":"LATMOS Hybrid simulations","dns":["impex.latmos.ipsl.fr"],"methods":["/Methods_LATMOS.wsdl"],"tree":["/tree.xml"],"protocol":["http"],"info":"http://impex.latmos.ipsl.fr/LatHyS.htm"},
		            {"id":"spase://IMPEX/Repository/SINP","type":"simulation","name":"SINP","description":"SINP Paraboloid Model simulations","dns":["smdc.sinp.msu.ru"],"methods":["/impex/SINP_methods.wsdl"],"tree":["/impex/SINP_tree.xml"],"protocol":["http"],"info":"http://smdc.sinp.msu.ru/index.py?nav=model-para"},
		            {"id":"spase://IMPEX/Repository/AMDA","type":"observation","name":"AMDA","description":"AMDA observational database","dns":["cdpp1.cesr.fr"],"methods":["/AMDA/php/AMDA_METHODS_WSDL.php?wsdl"],"tree":["/AMDA/data/WSRESULT/getObsDataTree_LocalParams.xml"],"protocol":["http"],"info":"http://clweb.cesr.fr/webservice.html"},
		            {"id":"spase://IMPEX/Repository/CLWEB","type":"observation","name":"CLWeb","description":"CLWeb observational databases","dns":["clweb.cesr.fr"],"methods":["/Methods_CLWEB.wsdl"],"tree":["/clweb_tree.xml"],"protocol":["http"],"info":"http://clweb.cesr.fr/webservice.html"}],
		        "tools":[
		            {"name":"AMDA","description":"Multi-mission data analysis tool for space plasma physics","url":"http://amda.cdpp.eu","info":"http://amda.cdpp.eu/help.html"},
		            {"name":"CLWeb","description":"Multi-mission space plasma data plotting tool","url":"http://clweb.cesr.fr","info":"http://clweb.cesr.fr/clweb_poster.pdf"},
		            {"name":"3DView","description":"3D multi-mission visualisation tool","url":"http://3dview.cdpp.eu","info":"http://3dview.cdpp.eu/other/cdpp3dview_tutorial.pdf"}]};
	
		$controller('configCtrl', {$scope: scope, configService: cService, userService: uService, $state: state, config: cfg});
	}));
	
	it('should inject services', function(){
		expect(scope.vm.configService).toBeDefined()
		expect(scope.vm.userService).toBeDefined()
	});
	
	it('should resolve config', function(){
		expect(scope.vm.configService.config).toBeDefined()
	});
	
	it('should resolve user', function(){
		expect(scope.vm.userService.user).toBeDefined()
	});
	
	
});






