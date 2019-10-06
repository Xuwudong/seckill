-- 秒杀执行存储过程
delimiter $$

-- 定义存储过程
-- row_count() 返回上一条修改类型SQL的影响条数
-- row_count() 0:未修改数据； > 0:表示影响的条数；<0:sql错误，未执行的修改sql

create procedure `seckill`.`execute_seckill`
    (in v_seckill_id bigint, in v_phone bigint,
        in v_kill_time timestamp ,out r_result int)

    begin
        declare insert_count int default 0;
        start transaction;
        insert ignore into success_killed(seckill_id,user_phone,create_time,state)
            value (v_seckill_id,v_phone,v_kill_time,0);
        select row_count() into insert_count;
        if(insert_count = 0) then
            rollback ;
            set r_result = -1;
        elseif (insert_count < 0) then
            rollback;
            set r_result = -2;
        else
            update seckill set number = number-1
            where seckill_id = v_seckill_id
                and end_time > v_kill_time
                and start_time < v_kill_time
                and number > 0;
            select row_count() into insert_count;
            if(insert_count = 0) then
                rollback;
                set r_result = 0;
            elseif (insert_count < 0) then
                rollback;
                set r_result = -2;
            else
                commit;
                set r_result = 1;
            end if;
        end if;
    end;
$$

-- 定义存储过程结束
delimiter ;
set @r_result = -3;

call seckill.execute_seckill(1001,18812345678,now(),@r_result);

select @r_result;