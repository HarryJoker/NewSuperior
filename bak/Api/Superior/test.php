<?php     
// Check MongoDB PHP driver. 		
if (!class_exists('MongoDB\Driver\Manager')) 		
{ 			
	$this->error('The MongoDB PECL extension has not been installed or enabled'); 		
}
?>
