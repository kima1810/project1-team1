INSERT INTO "User" (username,passwordHash,firstName,lastName,email,phoneNumber) VALUES
	 ('billy','$argon2id$v=19$m=131072,t=3,p=2$1dNccVqhJvHhivDKY6Mw1g$8VdpR1F7l7y0t6aO8Xe0qp5lbja/kKMksmUYs6Fsz0E','Billy','Joe','billy@test.com','1234567890'),
	 ('johndoe','$argon2id$v=19$m=131072,t=3,p=2$PmZ4P7DT8TlONg6/tnsUYg$4wF108KA+AW5n+hUNBPwDKpD+fUj7PUfl1XhHqwzau8','John','Doe','johndoe@test.com','(201)123-9857'),
	 ('sally','$argon2id$v=19$m=131072,t=3,p=2$MQ65OrqBOBX/6YEouym9yw$yWTVnMguY3YE/oXpGFeL1j1+ut3sfQ2lXK7ob6+4TQs','Sally','Test','sally@test.com','(732)890-3921'),
	 ('slagathor','$argon2id$v=19$m=131072,t=3,p=2$odhYjsjMUTX19VtCd/5gow$woRCTZOmLQxEdPeXr/GZZw+UOsFCkhOqDLvCgjNyfVY','Slagathor','Null','slagathor@test.com','+1 (888)123-4567');

INSERT INTO Account (accountNumber,pinHash,accountType,balance,username) VALUES
	 (144449008512,'$argon2id$v=19$m=131072,t=3,p=2$vqMhzGNNAq2uPAGq9qWpeQ$cwueU9a6csb1Q9iaSA04POn2s27lWK+D0j71EuYCBLU','SAVINGS',0.0,'johndoe'),
	 (330501195205,'$argon2id$v=19$m=131072,t=3,p=2$Yv9TEIXjC99Gk12CcPM3LQ$K8WHX9S3ha6XV2tJ20GFX9xRuiGFlY7/m5V4l+OQnFU','SAVINGS',0.0,'slagathor'),
	 (717189745756,'$argon2id$v=19$m=131072,t=3,p=2$C8ZATq0FqbLWlj4c8/iqQw$s3S439d2RYg6Xk/ZeTITlN+0FkXBgSQIwH7RWVJjom8','CHECKING',0.0,'billy'),
	 (852795830105,'$argon2id$v=19$m=131072,t=3,p=2$+5mbdgFoe3UkthIPO4xhCQ$SHwTenjwTt9mNCxoBh7uChCEQgjDmziwn5z0PpyQ5j8','CHECKING',0.0,'slagathor'),
	 (926927922739,'$argon2id$v=19$m=131072,t=3,p=2$4sIC1bQxBkfXW+ziRbaM4g$jXqn7y3WA2icmyC+pfOMd1tf7Si2EUB736KGPWIKulk','CHECKING',0.0,'sally');