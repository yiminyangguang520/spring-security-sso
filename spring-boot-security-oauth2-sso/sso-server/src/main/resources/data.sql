 INSERT INTO `oauth_client_details`
 VALUES ('merryyou1', null, '$2a$10$D/YRvseRjOkO9dsk71Sa3OkvPH6c5RS85pEIJjZv.6BqX3GQRQmj6', 'all,read,write', 'authorization_code,refresh_token',
   'http://xxlssoclient1.com:8083/client1/login', null, '36000', '36000', null, "true");

 INSERT INTO `oauth_client_details` VALUES ('merryyou2', null, '$2a$10$5k3Mj9mSwAKk.Kyw19kE0.Ow5HDlar9VV4kA9YQug23ZW5I8sLv.2', 'all,read,write',
  'authorization_code,refresh_token', 'http://xxlssoclient2.com:8084/client2/login', null, '36000', '36000', null, "true");

 INSERT INTO `oauth_client_details` VALUES ('sampleClientId', null, '$2a$10$eU3ldMc.Cew0rw4/cK06PeC3RSGsafxT1oDfMZO0oJuBmIQJ.R72m', 'read,write,foo,bar',
  'implicit', null, null, '36000', '36000', null, false);
