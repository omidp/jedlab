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


-- DROP INDEX public.stories_uuid_idx;

CREATE INDEX stories_uuid_idx
  ON public.stories
  USING btree
  (uuid COLLATE pg_catalog."default");

----------------------(Done)
  
------------------------------------- 1396-02-15 


-- ALTER TABLE public.stories DROP COLUMN comment_enabled;

ALTER TABLE public.stories ADD COLUMN comment_enabled boolean;
update stories set comment_enabled = true;
ALTER TABLE public.stories ALTER COLUMN comment_enabled SET NOT NULL;
ALTER TABLE public.stories ALTER COLUMN comment_enabled SET DEFAULT false;

----------------------(Done)

------------------------------------- 1396-02-23

-- ALTER TABLE public.gist DROP COLUMN uuid;

ALTER TABLE public.gist ADD COLUMN uuid character varying(255);
update gist set uuid='c9bdcabc-536f-45c3-80da-011692b9335d' where id=4921;
update gist set uuid='856e46f7-054c-4b53-9880-21361e8d091b' where id=4922;
update gist set uuid='bec59b8c-54a8-47c3-ad25-f6eadd1123f3' where id=4923;
update gist set uuid='11c185ba-d3c6-422c-85ff-e188dc3c1952' where id=4924;
update gist set uuid='13342d7a-8cfc-4464-add8-b3e51be3f9bb' where id=7321;
update gist set uuid='cb46ac4d-2831-41cf-b91b-84a78c9c233b' where id=4981;
update gist set uuid='214f9b69-7da3-4875-8624-3b557dc057dc' where id=5011;
update gist set uuid='072725fa-ad8e-4e2f-8de8-38236e39a4d8' where id=5012;
update gist set uuid='0f6837fa-1167-4e76-ac17-5d49e091b1a9' where id=5013;
update gist set uuid='80e6d458-d837-4375-8f96-a546a3870702' where id=5014;
update gist set uuid='7d129bda-2313-4f13-ba46-3374b83ed5c4' where id=5016;
update gist set uuid='8fcb8e36-4e8b-43b5-923f-784da07490ad' where id=5017;
update gist set uuid='318ef409-1877-4558-ae76-3898842c421a' where id=5015;
update gist set uuid='26b92a14-c69f-45ff-ba65-195a980b48fe' where id=5022;
update gist set uuid='4d7ad8c3-37ca-453c-bbc2-205fed981d13' where id=5019;
update gist set uuid='4506175d-e3d6-43c8-9599-97302a4c39b3' where id=7606;
update gist set uuid='78ff550f-65d1-4f37-9969-69cc7043f3a2' where id=5020;
update gist set uuid='33f0c060-f541-49b5-adbe-120cc8d79788' where id=5021;
update gist set uuid='83520ea3-6b5b-43d8-a404-8398e9724af8' where id=6301;
update gist set uuid='4d870899-028e-4a41-8dfd-687e86b50b7d' where id=6302;
update gist set uuid='4ca9f06e-b3d6-40f7-a29e-4fe582a0a844' where id=6751;
update gist set uuid='d7361431-b405-4faf-a78c-1142b1624125' where id=7607;
update gist set uuid='c8d71b9d-fb44-402f-accc-0f5d005523c1' where id=7608;
update gist set uuid='f68361c1-4eaa-4b38-8b8c-961a9d167823' where id=7609;
update gist set uuid='443ebbd0-cd24-4176-a76f-29c7682ba72c' where id=7610;
update gist set uuid='edbee30d-0f75-484e-9369-ae27c6e9d503' where id=7611;
update gist set uuid='0573a035-9354-4ea8-a820-6d1ac2d22025' where id=7612;
update gist set uuid='d97d503c-1f30-4b94-9bd6-16162470af73' where id=7613;
update gist set uuid='327cbeb7-c310-44ee-b4e1-49ca8fbf4016' where id=7614;
update gist set uuid='51986d84-308f-41ee-a7cb-6eb0dbc6c02f' where id=7615;
update gist set uuid='aa3de4ad-20a5-428b-84e7-8f4fa83ebe84' where id=10426;
update gist set uuid='8f208737-5921-4a74-bec2-45af3e4fcc50' where id=10427;
update gist set uuid='26218785-c214-4d6f-b7bc-7cbab7ae82ef' where id=10428;
update gist set uuid='7f8a2d53-36a0-4984-b6f1-101291538d64' where id=10429;
update gist set uuid='1372f087-b413-4a20-9e1a-ae1fe2a03d19' where id=10430;
ALTER TABLE public.gist ALTER COLUMN uuid SET NOT NULL;
CREATE INDEX gist_uuid_idx
  ON public.gist
  USING btree
  (uuid COLLATE pg_catalog."default");
  
  ------------------------------Done 
  
  ALTER TABLE public.curates ALTER COLUMN url type character varying(2000);
  ALTER TABLE public.curates ALTER COLUMN title type character varying(2000);
  ALTER TABLE public.curates ALTER COLUMN description type character varying(2000);
  ALTER TABLE public.curates ALTER COLUMN keywords type character varying(2000);
  ALTER TABLE public.curates ALTER COLUMN image_url type character varying(2000);
  
  ----------------------------DONE
  update member set discriminator = 'I' where id=1;