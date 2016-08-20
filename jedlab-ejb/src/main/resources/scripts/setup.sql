-- nimda@java
INSERT INTO member (id, is_active, email_address, join_date, user_pass, user_name, activation_code, recover_passwd_code)
VALUES (1, true, 'admin@jedlab.ir', '2016-08-05 22:56:39.198418', 'jQ5SHBmn6FuvNR9/qYGwfg==', 'admin', NULL, NULL);

ALTER TABLE member_course
  ADD CONSTRAINT member_course_chapter_id_course_id_member_id_key UNIQUE(chapter_id, course_id, member_id);