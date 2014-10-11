  
IMAC_Path=$(pwd)"/../../"

  name="receiving"
  path="$IMAC_Path/mote/c-header"
  targetFolder="$path/../$name"
  package="package mote.$name;"
  (
    cd $path
    if [ "$#" == 0 ]
    then
      headerlist=($(ls *.h))
    else
      headerlist=$@
    fi

    for fileName in ${headerlist[@]};
    do
      structName=$(basename $fileName .h)
      echo "generating $name Class for $structName"
      javaName=$structName.java
      javaClassNamen=$structName.class
      temp="temp-file"
      mig java -target=null -java-classname=$structName $fileName $structName -o $javaName
      echo "$package" > $temp
      cat $javaName >> $temp
      mv $temp $javaName
      javac $javaName
      mv $javaName $javaClassNamen $targetFolder
  done

  )
