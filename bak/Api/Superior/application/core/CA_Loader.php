<?php

defined('BASEPATH') OR exit('No direct script access allowed');

class CA_Loader extends CI_Loader
{

    /**
     * Database Loader
     *
     * @param	mixed	$params		Database configuration options
     * @param	bool	$return 	Whether to return the database object
     * @param	bool	$query_builder	Whether to enable Query Builder
     * 					(overrides the configuration setting)
     *
     * @return	object|bool	Database object if $return is set to TRUE,
     * 					FALSE on failure, CI_Loader instance in any other case
     */
    public function database($params = '', $return = FALSE, $query_builder = NULL)
    {
        // Grab the super object
        $CI = & get_instance();

        // Do we even need to load the database class?
        if ($return === FALSE && $query_builder === NULL && isset($CI->db) && is_object($CI->db) && !empty($CI->db->conn_id))
        {
            return FALSE;
        }

        require_once(BASEPATH . 'database/DB.php');

        require_once(BASEPATH . 'database/DB_driver.php');

        if (!isset($query_builder) OR $query_builder === TRUE)
        {
            require_once(BASEPATH . 'database/DB_query_builder.php');

            if (!class_exists('CI_DB', FALSE))
            {
                $query_builder_path = APPPATH . 'libraries/database/CA_DB_query_builder.php';
                if (file_exists($query_builder_path))
                {
                    require_once($query_builder_path);
                }
            }
        }

        if ($return === TRUE)
        {
            return DB($params, $query_builder);
        }

        // Initialize the db variable. Needed to prevent
        // reference errors with some configurations
        $CI->db = '';

        // Load the DB class
        $CI->db = & DB($params, $query_builder);

        return $this;
    }

}