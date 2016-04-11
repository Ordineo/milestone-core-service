INSERT INTO objective(id,title,description,objectiveType) VALUES (1,'Spring Boot','Lorem Ipsum is slechts een proeftekst uit het drukkerij- en zetterijwezen. Lorem Ipsum is de standaard proeftekst in deze bedrijfstak sinds de 16e eeuw, toen een onbekende drukker een zethaak met letters','TRAINING');
INSERT INTO objective_tags(objective_id, tag) values (1, 'Java certificate');
INSERT INTO objective_tags(objective_id, tag) values (1, 'spring');

insert into Milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) values (1, 1, 'gide', '2016-02-01', '2016-12-31', '2016-03-01', 'time to upgrade from java 6 to java 8 certificate.');
