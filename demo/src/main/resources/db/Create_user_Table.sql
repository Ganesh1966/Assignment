
CREATE TABLE IF NOT EXISTS public.user
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(150) NOT NULL,
    current_amount character varying(15) NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
    )
    TABLESPACE pg_default;
ALTER TABLE public.user
    OWNER to "dataclog-prod-user";


alter table public.user add account_status character varying(15) NOT NULL;

alter table public.user add age character varying(3) not null ;
