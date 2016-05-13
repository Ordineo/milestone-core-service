INSERT INTO objective(id,title,description,objectiveType) VALUES (1,'Spring Boot','Lorem Ipsum is slechts een proeftekst uit het drukkerij- en zetterijwezen. Lorem Ipsum is de standaard proeftekst in deze bedrijfstak sinds de 16e eeuw, toen een onbekende drukker een zethaak met letters','TRAINING');
INSERT INTO objective_tags(objective_id, tag) VALUES (1, 'java');
INSERT INTO objective_tags(objective_id, tag) VALUES (1, 'spring');

INSERT INTO objective(id, title, description, objectiveType) VALUES (2, 'Java Certificate', 'description certificate', 'CERTIFICATE');
INSERT INTO objective_tags(objective_id, tag) VALUES (2, 'java');

INSERT INTO objective(id, title, description, objectiveType) VALUES (3, 'Scrum Master', 'description certificate', 'CERTIFICATE');
INSERT INTO objective_tags(objective_id, tag) VALUES (3, 'scrum');

INSERT INTO milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) VALUES (1, 2, 'gide', '2016-02-01', '2016-12-31', '2016-03-01', 'time to upgrade from java 6 to java 8 certificate.');
INSERT INTO milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) VALUES (2, 1, 'gide', '2017-02-01', '2017-12-31', null, 'extra spring milestone');

INSERT INTO milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) VALUES (3, 2, 'Rydg', '2016-02-05', '2016-12-31', '2016-03-01', 'Getting my first java certificate');
INSERT INTO milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) VALUES (4, 1, 'Rydg', '2017-06-01', '2016-05-07', null, 'extra spring milestone');
INSERT INTO milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) VALUES (5, 3, 'Rydg', '2016-02-05', '2016-12-31', '2016-03-01', 'Getting a scrum master certificate');

INSERT INTO comment(id,createdate, message, username, milestone_comment) VALUES (1,'2011-07-11','Test message','PhDa', 1);




-- Test milestone/objective/comment
INSERT INTO milestone(id, objective_id, username, createdate, duedate, enddate, moreinformation) VALUES (100, 1, 'Test', '2017-02-01', '2017-12-31', null, 'extra spring milestone');
INSERT INTO comment(id,createdate, message, username,milestone_comment) VALUES (100,'2011-07-11','Test message','PhDa',100);

