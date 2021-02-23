<?php

defined('BASEPATH') OR exit('No direct script access allowed');

class CI_DB extends CA_DB_query_builder
{
    
}

class CA_DB_query_builder extends CI_DB_query_builder
{

    /**
     * Insert_On_Duplicate_Key_Update_Batch
     *
     * Compiles batch insert strings and runs the queries
     *
     * @param	string	$table	Table to insert into
     * @param	array	$set 	An associative array of insert values
     * @param	bool	$escape	Whether to escape values and identifiers
     * @return	int	Number of rows inserted or FALSE on failure
     */
    public function insert_on_duplicate_update_batch($table = '', $set = NULL, $escape = NULL)
    {
        if ($set !== NULL)
        {
            $this->set_insert_batch($set, '', $escape);
        }
        
        if (count($this->qb_set) === 0)
        {
            // No valid data array. Folds in cases where keys and values did not match up
            return ($this->db_debug) ? $this->display_error('db_must_use_set') : FALSE;
        }
        
        if ($table === '')
        {
            if (!isset($this->qb_from[0]))
            {
                return ($this->db_debug) ? $this->display_error('db_must_set_table') : FALSE;
            }
            $table = $this->qb_from[0];
        }
        
        // Batch this baby
        $affected_rows = 0;
        for ($i = 0, $total = count($this->qb_set); $i < $total; $i += 100)
        {
            $this->query($this->_insert_on_duplicate_key_update_batch($this->protect_identifiers($table, TRUE, $escape, FALSE), $this->qb_keys, array_slice($this->qb_set, $i, 100)));
            $affected_rows += $this->affected_rows();
        }
        $this->_reset_write();
        return $affected_rows;
    }

    /**
     * Insert on duplicate key update batch statement
     *
     * Generates a platform-specific insert string from the supplied data
     *
     * @param	string	$table	Table name
     * @param	array	$keys	INSERT keys
     * @param	array	$values	INSERT values
     * @return	string
     */
    private function _insert_on_duplicate_key_update_batch($table, $keys, $values)
    {
        foreach ($keys as $num => $key)
        {
            $update_fields[] = $key . '= VALUES(' . $key . ')';
        }
        
        return "INSERT INTO " . $table . " (" . implode(', ', $keys) . ") VALUES " . implode(', ', $values) . " ON DUPLICATE KEY UPDATE " . implode(', ', $update_fields);
    }


    /**
     * Insert Ingore
     *
     * What this basically does is compile a query as an INSERT and then simply
     * amend the string "INSERT" at the start of the query to be "INSERT IGNORE".
     * 
     * INSERT IGNORE means that any errors for duplicate rows will be suppressed to
     * warnings. 
     * 
     * @access    public
     * @param    string    the table name
     * @param    string    optional insert data
     * @return    object
     */
    function insert_ignore($table, $data = FALSE)
    {
      if($data) $this->set($data);

      $query_string = 'INSERT IGNORE'.substr($this->get_compiled_insert($table), 6);

      var_dump($query_string);
      return $this->query($query_string);
    }


    /**
     * Insert_Batch
     *
     * Compiles batch insert strings and runs the queries
     *
     * @param   string  $table  Table to insert into
     * @param   array   $set    An associative array of insert values
     * @param   bool    $escape Whether to escape values and identifiers
     * @return  int Number of rows inserted or FALSE on failure
     */
    public function insert_ignore_batch($table, $set = NULL, $escape = NULL, $batch_size = 100)
    {
        if ($set === NULL)
        {
            if (empty($this->qb_set))
            {
                return ($this->db_debug) ? $this->display_error('db_must_use_set') : FALSE;
            }
        }
        else
        {
            if (empty($set))
            {
                return ($this->db_debug) ? $this->display_error('insert_batch() called with no data') : FALSE;
            }

            $this->set_insert_batch($set, '', $escape);
        }

        if (strlen($table) === 0)
        {
            if ( ! isset($this->qb_from[0]))
            {
                return ($this->db_debug) ? $this->display_error('db_must_set_table') : FALSE;
            }

            $table = $this->qb_from[0];
        }

        // Batch this baby
        $affected_rows = 0;
        for ($i = 0, $total = count($this->qb_set); $i < $total; $i += $batch_size)
        {
            $_query = $this->_insert_batch($this->protect_identifiers($table, TRUE, $escape, FALSE), $this->qb_keys, array_slice($this->qb_set, $i, $batch_size));
            $_query = str_replace('INSERT INTO','INSERT IGNORE INTO',$_query);
            if ($this->query($_query))
            {
                $affected_rows += $this->affected_rows();
            }
        }
        $this->_reset_write();
        return $affected_rows;
    }

}