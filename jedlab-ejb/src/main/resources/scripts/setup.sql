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
  
  
  -- DROP INDEX public.member_user_name_idx;

CREATE INDEX member_user_name_idx
  ON public.member
  USING btree
  (user_name COLLATE pg_catalog."default");

  -- DROP INDEX public.member_email_address_idx;

CREATE INDEX member_email_address_idx
  ON public.member
  USING btree
  (email_address COLLATE pg_catalog."default");
---------NEW
  
  -- ALTER TABLE public.course_rating DROP CONSTRAINT course_rating_course_id_member_id_key;

ALTER TABLE public.course_rating
  ADD CONSTRAINT course_rating_course_id_member_id_key UNIQUE(course_id, member_id);
  
  
  
  
 
--------------------------- 2017


-- ALTER TABLE public.member DROP COLUMN discriminator;

ALTER TABLE public.member ADD COLUMN discriminator character varying(1);
update public.member set discriminator = 'S';
ALTER TABLE public.member ALTER COLUMN discriminator SET NOT NULL;
drop table student;
drop table instructor; 


-- ALTER TABLE public.course DROP COLUMN published;

ALTER TABLE public.course ADD COLUMN published boolean;
update course set published = true;
ALTER TABLE public.course ALTER COLUMN published SET NOT NULL;
ALTER TABLE public.course ALTER COLUMN published SET DEFAULT false;

update course set instructor_id = 1;