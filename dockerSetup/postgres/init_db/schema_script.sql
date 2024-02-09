-- Step 1 : Create table schema_list in public schema
CREATE TABLE public.schema_list (schema_name text);
-- Step 2 : Create 50 schema
DO $$ 
DECLARE
    schema_name text;
BEGIN
    FOR i IN 1..50 LOOP
		schema_name := 'schema_' || i;
		
        EXECUTE 'CREATE SCHEMA ' || schema_name;
		EXECUTE 'INSERT INTO public.schema_list(schema_name) VALUES(''' || schema_name || ''')';
    END LOOP;
END $$;

-- Step 2: Create 10 tables in each schema
DO $$ 
DECLARE
    schema_name text;
BEGIN
    FOR i IN 1..50 LOOP
        schema_name := 'schema_' || i;

        FOR j IN 1..10 LOOP
            EXECUTE 'CREATE TABLE ' || schema_name || '.table_' || j || ' (id serial primary key, column1 text, column2 integer)';
        END LOOP;
    END LOOP;
END $$;