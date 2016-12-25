-- nimda@java
INSERT INTO member (id, is_active, email_address, join_date, user_pass, user_name, activation_code, recover_passwd_code)
VALUES (1, true, 'admin@jedlab.ir', '2016-08-05 22:56:39.198418', 'jQ5SHBmn6FuvNR9/qYGwfg==', 'admin', NULL, NULL);

ALTER TABLE member_course
  ADD CONSTRAINT member_course_chapter_id_course_id_member_id_key UNIQUE(chapter_id, course_id, member_id);
  
  insert into student(member_id) select id from member;
  
  
  ------------------ 24 Dec 2016
  
  -- ALTER TABLE public.member_course DROP COLUMN can_download;

ALTER TABLE public.member_course ADD COLUMN can_download boolean;
ALTER TABLE public.member_course ALTER COLUMN can_download SET DEFAULT false;
update public.member_course set can_download = false;
ALTER TABLE public.member_course ALTER COLUMN can_download SET NOT NULL;

ALTER TABLE public.course ADD COLUMN download_price bigint;
ALTER TABLE public.course ALTER COLUMN download_price SET DEFAULT 1000;
update public.course set download_price = 1000;
ALTER TABLE public.course ALTER COLUMN download_price SET NOT NULL;


ALTER TABLE public.invoice
  ADD CONSTRAINT invoice_member_id_course_id_key UNIQUE(member_id, course_id);